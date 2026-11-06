package com.ripple.lilypath.domain.repository

import com.ripple.lilypath.domain.model.Season

interface ProgressRepository {

    fun solvedPonds(): Set<Int>

    fun isSolved(index: Int): Boolean

    fun markSolved(index: Int, hints: Int)

    fun hintsUsed(index: Int): Int

    fun nextUnsolved(season: Season): Int

    fun unlockedSeasons(): Set<Season>

    fun isTutorialDone(): Boolean

    fun markTutorialDone()

    fun reset()
}
