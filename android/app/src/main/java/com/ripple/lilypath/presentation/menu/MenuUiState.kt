package com.ripple.lilypath.presentation.menu

import com.ripple.lilypath.core.config.GameConfig
import com.ripple.lilypath.domain.model.HintFrequency
import com.ripple.lilypath.domain.model.PondNodeKind
import com.ripple.lilypath.domain.model.Season

data class PondTrailNode(
    val index: Int,
    val kind: PondNodeKind
)

data class MenuUiState(
    val loading: Boolean = true,
    val selectedSeason: Season = Season.SPRING,
    val unlockedSeasons: Set<Season> = setOf(Season.SPRING),
    val solvedCount: Int = 0,
    val totalPonds: Int = GameConfig.TOTAL_PONDS,
    val frogCount: Int = 0,
    val totalFrogs: Int = 4,
    val nextPond: Int = 0,
    val trail: List<PondTrailNode> = emptyList(),
    val settingsVisible: Boolean = false,
    val resetConfirmVisible: Boolean = false,
    val volume: Int = 70,
    val haptics: Boolean = true,
    val hintFrequency: HintFrequency = HintFrequency.SOMETIMES,
    val scrollRequest: Int = 0,
    val scrollTarget: Int = 0
) {
    val statsVisible: Boolean = solvedCount > 0 && frogCount > 0
}
