package com.ripple.lilypath.data.repository

import com.ripple.lilypath.core.config.GameConfig
import com.ripple.lilypath.data.local.PondPreferences
import com.ripple.lilypath.domain.model.Season
import com.ripple.lilypath.domain.repository.ProgressRepository
import com.ripple.lilypath.domain.usecase.UnlockSeasonUseCase

class ProgressRepositoryImpl(
    private val preferences: PondPreferences,
    private val unlockSeason: UnlockSeasonUseCase
) : ProgressRepository {

    override fun solvedPonds(): Set<Int> = preferences.solvedPonds()

    override fun isSolved(index: Int): Boolean = preferences.solvedPonds().contains(index)

    override fun markSolved(index: Int, hints: Int) {
        val updated = preferences.solvedPonds().toMutableSet()
        updated.add(index)
        preferences.storeSolved(updated)
        val previous = preferences.hints(index)
        if (previous == 0 || hints < previous) preferences.storeHints(index, hints)
    }

    override fun hintsUsed(index: Int): Int = preferences.hints(index)

    override fun nextUnsolved(season: Season): Int {
        val slot = Season.entries.indexOf(season).coerceAtLeast(0)
        val first = slot * GameConfig.SEASON_SIZE
        val solved = preferences.solvedPonds()
        val open = (first until first + GameConfig.SEASON_SIZE).firstOrNull { !solved.contains(it) }
        return open ?: first
    }

    override fun unlockedSeasons(): Set<Season> = unlockSeason.unlocked(preferences.solvedPonds())

    override fun isTutorialDone(): Boolean = preferences.tutorialDone()

    override fun markTutorialDone() {
        preferences.storeTutorialDone(true)
    }

    override fun reset() {
        preferences.clearProgress()
    }
}
