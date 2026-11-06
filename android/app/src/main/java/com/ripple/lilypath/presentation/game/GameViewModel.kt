package com.ripple.lilypath.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ripple.lilypath.core.config.GameConfig
import com.ripple.lilypath.data.local.PondFeedback
import com.ripple.lilypath.domain.model.HintFrequency
import com.ripple.lilypath.domain.model.PadCell
import com.ripple.lilypath.domain.model.Pond
import com.ripple.lilypath.domain.model.TrailPhase
import com.ripple.lilypath.domain.repository.ProgressRepository
import com.ripple.lilypath.domain.repository.SettingsRepository
import com.ripple.lilypath.domain.usecase.GeneratePondUseCase
import com.ripple.lilypath.domain.usecase.NextHintUseCase
import com.ripple.lilypath.domain.usecase.UnlockSeasonUseCase
import com.ripple.lilypath.domain.usecase.ValidateMoveUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val generatePond: GeneratePondUseCase,
    private val validateMove: ValidateMoveUseCase,
    private val nextHint: NextHintUseCase,
    private val unlockSeason: UnlockSeasonUseCase,
    private val feedback: PondFeedback
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events: Flow<GameEvent> = _events.receiveAsFlow()

    private var solveJob: Job? = null

    fun load(pondIndex: Int) {
        solveJob?.cancel()
        solveJob = null
        viewModelScope.launch {
            val pond = generatePond(pondIndex)
            val tutorial = pondIndex == 0 && !progress.isTutorialDone()
            _state.value = GameUiState(
                pond = pond,
                visited = listOf(pond.start),
                phase = if (tutorial) TrailPhase.TUTORIAL else TrailPhase.IDLE,
                hintCell = null,
                hintsUsed = 0,
                hintsEnabled = settings.hintFrequency() != HintFrequency.NEVER,
                pauseVisible = false,
                tutorialVisible = tutorial
            )
        }
    }

    fun onPadTouched(row: Int, col: Int) {
        val current = _state.value
        val pond = current.pond ?: return
        if (current.pauseVisible || current.solved) return
        val target = PadCell(row, col)
        when {
            validateMove.isRetrace(current.visited, target) -> retreat(pond, current.visited)
            validateMove.canAdvance(pond, current.visited, target) -> advance(pond, current.visited, target)
        }
    }

    fun onDragFinished() {
        val current = _state.value
        val pond = current.pond ?: return
        if (current.solved) return
        _state.update { it.copy(phase = resolvePhase(pond, it.visited, it.tutorialVisible)) }
    }

    fun undo() {
        val current = _state.value
        val pond = current.pond ?: return
        if (current.solved || current.visited.size <= 1) return
        retreat(pond, current.visited)
    }

    fun resetTrail() {
        val current = _state.value
        val pond = current.pond ?: return
        _state.update {
            it.copy(
                visited = listOf(pond.start),
                phase = if (it.tutorialVisible) TrailPhase.TUTORIAL else TrailPhase.IDLE,
                hintCell = null
            )
        }
    }

    fun useHint() {
        val current = _state.value
        val pond = current.pond ?: return
        if (!current.hintsEnabled || current.solved) return
        val suggestion = nextHint(pond, current.visited) ?: return
        _state.update { it.copy(hintCell = suggestion, hintsUsed = it.hintsUsed + 1) }
        advance(pond, current.visited, suggestion)
    }

    fun openPause() {
        _state.update { it.copy(pauseVisible = true) }
    }

    fun closePause() {
        _state.update { it.copy(pauseVisible = false) }
    }

    fun restartPond() {
        val pond = _state.value.pond ?: return
        _state.update {
            it.copy(
                visited = listOf(pond.start),
                phase = TrailPhase.IDLE,
                hintCell = null,
                hintsUsed = 0,
                pauseVisible = false
            )
        }
    }

    fun dismissTutorial() {
        if (!_state.value.tutorialVisible) return
        progress.markTutorialDone()
        _state.update {
            it.copy(
                tutorialVisible = false,
                phase = if (it.phase == TrailPhase.TUTORIAL) TrailPhase.IDLE else it.phase
            )
        }
    }

    fun pause() {
        solveJob?.cancel()
        solveJob = null
        feedback.release()
    }

    private fun advance(pond: Pond, visited: List<PadCell>, target: PadCell) {
        val updated = visited + target
        val solved = validateMove.isSolved(pond, updated)
        val leftTutorial = _state.value.tutorialVisible
        _state.update {
            it.copy(
                visited = updated,
                phase = if (solved) TrailPhase.SOLVED else TrailPhase.TRACING,
                tutorialVisible = false
            )
        }
        feedback.playNote(updated.size, settings.volume())
        feedback.tick(settings.haptics())
        if (leftTutorial) progress.markTutorialDone()
        if (solved) finish(pond)
    }

    private fun retreat(pond: Pond, visited: List<PadCell>) {
        if (visited.size <= 1) return
        val updated = visited.dropLast(1)
        _state.update {
            it.copy(
                visited = updated,
                phase = resolvePhase(pond, updated, it.tutorialVisible),
                hintCell = null
            )
        }
        feedback.tick(settings.haptics())
    }

    private fun resolvePhase(pond: Pond, visited: List<PadCell>, tutorial: Boolean): TrailPhase = when {
        validateMove.isSolved(pond, visited) -> TrailPhase.SOLVED
        tutorial -> TrailPhase.TUTORIAL
        validateMove.isStuck(pond, visited) -> TrailPhase.STUCK
        else -> TrailPhase.IDLE
    }

    private fun finish(pond: Pond) {
        solveJob?.cancel()
        solveJob = viewModelScope.launch {
            val hints = _state.value.hintsUsed
            progress.markSolved(pond.index, hints)
            if (pond.index == 0) progress.markTutorialDone()
            val seasonDone = unlockSeason.seasonCompletedBy(pond.index, progress.solvedPonds())
            delay(GameConfig.SOLVE_DELAY_MS)
            _events.send(
                GameEvent.Solved(
                    pondIndex = pond.index,
                    pads = pond.requiredPads,
                    hintsUsed = hints,
                    seasonCompleted = seasonDone
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        feedback.release()
    }
}
