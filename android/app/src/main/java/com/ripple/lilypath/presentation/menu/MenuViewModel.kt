package com.ripple.lilypath.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ripple.lilypath.core.config.GameConfig
import com.ripple.lilypath.domain.model.HintFrequency
import com.ripple.lilypath.domain.model.PondNodeKind
import com.ripple.lilypath.domain.model.Season
import com.ripple.lilypath.domain.repository.ProgressRepository
import com.ripple.lilypath.domain.repository.SettingsRepository
import com.ripple.lilypath.domain.usecase.UnlockSeasonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val unlockSeason: UnlockSeasonUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val solved = progress.solvedPonds()
            val unlocked = unlockSeason.unlocked(solved)
            val completed = unlockSeason.completed(solved)
            val season = if (unlocked.contains(_state.value.selectedSeason)) {
                _state.value.selectedSeason
            } else {
                Season.SPRING
            }
            _state.update { current ->
                current.copy(
                    loading = false,
                    selectedSeason = season,
                    unlockedSeasons = unlocked,
                    solvedCount = solved.size,
                    frogCount = completed.size,
                    nextPond = progress.nextUnsolved(season),
                    trail = buildTrail(season, solved),
                    volume = settings.volume(),
                    haptics = settings.haptics(),
                    hintFrequency = settings.hintFrequency()
                )
            }
        }
    }

    fun selectSeason(season: Season) {
        if (!_state.value.unlockedSeasons.contains(season)) return
        viewModelScope.launch {
            val solved = progress.solvedPonds()
            _state.update { current ->
                current.copy(
                    selectedSeason = season,
                    nextPond = progress.nextUnsolved(season),
                    trail = buildTrail(season, solved)
                )
            }
        }
    }

    fun scrollToCurrent() {
        _state.update { current ->
            val target = current.trail.indexOfFirst { it.kind != PondNodeKind.SOLVED }
            current.copy(
                scrollRequest = current.scrollRequest + 1,
                scrollTarget = if (target >= 0) target else 0
            )
        }
    }

    fun openSettings() {
        _state.update { it.copy(settingsVisible = true) }
    }

    fun closeSettings() {
        _state.update { it.copy(settingsVisible = false, resetConfirmVisible = false) }
    }

    fun setVolume(value: Int) {
        viewModelScope.launch {
            settings.setVolume(value)
            _state.update { it.copy(volume = settings.volume()) }
        }
    }

    fun setHaptics(enabled: Boolean) {
        viewModelScope.launch {
            settings.setHaptics(enabled)
            _state.update { it.copy(haptics = settings.haptics()) }
        }
    }

    fun setHintFrequency(value: HintFrequency) {
        viewModelScope.launch {
            settings.setHintFrequency(value)
            _state.update { it.copy(hintFrequency = settings.hintFrequency()) }
        }
    }

    fun askResetConfirm() {
        _state.update { it.copy(resetConfirmVisible = true) }
    }

    fun cancelReset() {
        _state.update { it.copy(resetConfirmVisible = false) }
    }

    fun confirmReset() {
        viewModelScope.launch {
            progress.reset()
            _state.update { it.copy(resetConfirmVisible = false, settingsVisible = false) }
            refresh()
        }
    }

    private fun buildTrail(season: Season, solved: Set<Int>): List<PondTrailNode> {
        val slot = Season.entries.indexOf(season).coerceAtLeast(0)
        val first = slot * GameConfig.SEASON_SIZE
        val current = (first until first + GameConfig.SEASON_SIZE).firstOrNull { !solved.contains(it) }
        return (first until first + GameConfig.SEASON_SIZE).map { index ->
            val kind = when {
                solved.contains(index) -> PondNodeKind.SOLVED
                index == current -> PondNodeKind.CURRENT
                else -> PondNodeKind.LOCKED
            }
            PondTrailNode(index, kind)
        }
    }
}
