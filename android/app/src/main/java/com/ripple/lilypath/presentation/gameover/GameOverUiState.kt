package com.ripple.lilypath.presentation.gameover

import com.ripple.lilypath.domain.model.FrogFriend
import com.ripple.lilypath.domain.model.Pond

data class GameOverUiState(
    val pond: Pond? = null,
    val pondNumber: Int = 1,
    val pads: Int = 0,
    val hintsUsed: Int = 0,
    val solvedCount: Int = 0,
    val paradeStep: Int = 0,
    val newFrog: FrogFriend? = null,
    val nextPondIndex: Int = 0,
    val hasNextPond: Boolean = false
) {
    val hintCardVisible: Boolean = hintsUsed > 0

    val statsVisible: Boolean = pads > 0 && solvedCount > 0
}
