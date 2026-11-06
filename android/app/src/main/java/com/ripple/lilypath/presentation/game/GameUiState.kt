package com.ripple.lilypath.presentation.game

import com.ripple.lilypath.domain.model.PadCell
import com.ripple.lilypath.domain.model.Pond
import com.ripple.lilypath.domain.model.TrailPhase

data class GameUiState(
    val pond: Pond? = null,
    val visited: List<PadCell> = emptyList(),
    val phase: TrailPhase = TrailPhase.LOADING,
    val hintCell: PadCell? = null,
    val hintsUsed: Int = 0,
    val hintsEnabled: Boolean = true,
    val pauseVisible: Boolean = false,
    val tutorialVisible: Boolean = false
) {
    val visitedPads: Int = pond?.let { board -> visited.count { board.pads.contains(it) } } ?: 0

    val totalPads: Int = pond?.requiredPads ?: 0

    val canUndo: Boolean = visited.size > 1

    val homeReady: Boolean = pond != null && visitedPads == totalPads - 1

    val solved: Boolean = phase == TrailPhase.SOLVED

    val stuck: Boolean = phase == TrailPhase.STUCK
}

sealed interface GameEvent {
    data class Solved(
        val pondIndex: Int,
        val pads: Int,
        val hintsUsed: Int,
        val seasonCompleted: Boolean
    ) : GameEvent
}
