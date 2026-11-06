package com.ripple.lilypath.domain.usecase

import com.ripple.lilypath.core.config.GameConfig
import com.ripple.lilypath.domain.model.Season

class UnlockSeasonUseCase {

    fun completed(solved: Set<Int>): Set<Season> {
        val done = LinkedHashSet<Season>()
        Season.entries.forEachIndexed { slot, season ->
            val first = slot * GameConfig.SEASON_SIZE
            val range = first until first + GameConfig.SEASON_SIZE
            if (range.all { solved.contains(it) }) done.add(season)
        }
        return done
    }

    fun unlocked(solved: Set<Int>): Set<Season> {
        val done = completed(solved)
        val open = LinkedHashSet<Season>()
        open.add(Season.SPRING)
        Season.entries.forEachIndexed { slot, season ->
            if (done.contains(season) && slot + 1 < Season.entries.size) {
                open.add(Season.entries[slot + 1])
            }
        }
        return open
    }

    fun seasonCompletedBy(index: Int, solved: Set<Int>): Boolean {
        val slot = index / GameConfig.SEASON_SIZE
        val first = slot * GameConfig.SEASON_SIZE
        return (first until first + GameConfig.SEASON_SIZE).all { solved.contains(it) }
    }
}
