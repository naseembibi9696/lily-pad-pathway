package com.ripple.lilypath.core.navigation

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data class Game(val pondIndex: Int) : Screen

    data class GameOver(
        val pondIndex: Int,
        val pads: Int,
        val hintsUsed: Int,
        val unlockedSeason: Boolean
    ) : Screen

    data object FrogFriends : Screen
}
