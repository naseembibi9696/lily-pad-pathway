package com.ripple.lilypath.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ripple.lilypath.core.config.GameConfig
import com.ripple.lilypath.data.sample.SampleData
import com.ripple.lilypath.domain.repository.ProgressRepository
import com.ripple.lilypath.domain.usecase.GeneratePondUseCase
import com.ripple.lilypath.domain.usecase.UnlockSeasonUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameOverViewModel(
    private val progress: ProgressRepository,
    private val generatePond: GeneratePondUseCase,
    private val unlockSeason: UnlockSeasonUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private var paradeJob: Job? = null

    fun load(pondIndex: Int, pads: Int, hintsUsed: Int, seasonCompleted: Boolean) {
        paradeJob?.cancel()
        viewModelScope.launch {
            val pond = generatePond(pondIndex)
            val solved = progress.solvedPonds()
            val season = pond.season
            val frog = if (seasonCompleted) {
                SampleData.frogFriends.firstOrNull { it.season == season }?.copy(unlocked = true)
            } else {
                null
            }
            _state.value = GameOverUiState(
                pond = pond,
                pondNumber = pondIndex + 1,
                pads = pads,
                hintsUsed = hintsUsed,
                solvedCount = solved.size,
                paradeStep = 0,
                newFrog = frog,
                nextPondIndex = (pondIndex + 1).coerceAtMost(GameConfig.TOTAL_PONDS - 1),
                hasNextPond = pondIndex + 1 < GameConfig.TOTAL_PONDS &&
                    unlockSeason.unlocked(solved).contains(generatePond.seasonOf(pondIndex + 1))
            )
            startParade(pond.solution.size)
        }
    }

    fun pause() {
        paradeJob?.cancel()
        paradeJob = null
    }

    private fun startParade(steps: Int) {
        paradeJob?.cancel()
        paradeJob = viewModelScope.launch {
            while (isActive) {
                for (step in 0 until steps) {
                    _state.update { it.copy(paradeStep = step) }
                    delay(GameConfig.HOP_MS)
                }
                delay(GameConfig.HOP_MS * 3)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        paradeJob?.cancel()
    }
}
