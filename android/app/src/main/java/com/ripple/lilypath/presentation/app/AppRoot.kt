package com.ripple.lilypath.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ripple.lilypath.core.di.AppViewModelFactory
import com.ripple.lilypath.core.navigation.Screen
import com.ripple.lilypath.presentation.friends.FrogFriendsScreen
import com.ripple.lilypath.presentation.game.GameScreen
import com.ripple.lilypath.presentation.gameover.GameOverScreen
import com.ripple.lilypath.presentation.menu.MenuScreen
import com.ripple.lilypath.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) {
        appViewModel.back()
    }

    AnimatedContent(
        targetState = screen,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            if (targetState is Screen.Game) {
                (fadeIn(tween(320)) + slideInVertically { full -> full / 8 }) togetherWith
                    fadeOut(tween(260))
            } else {
                fadeIn(tween(450)) togetherWith fadeOut(tween(350))
            }
        },
        label = "screenHost"
    ) { current ->
        when (current) {
            is Screen.Splash -> SplashScreen(
                onFinished = { appViewModel.replaceTop(Screen.Menu) }
            )

            is Screen.Menu -> MenuScreen(
                onPlay = { pondIndex -> appViewModel.navigate(Screen.Game(pondIndex)) },
                onOpenFriends = { appViewModel.navigate(Screen.FrogFriends) }
            )

            is Screen.Game -> GameScreen(
                pondIndex = current.pondIndex,
                onBack = { appViewModel.back() },
                onMenu = { appViewModel.toMenu() },
                onSolved = { pondIndex, pads, hints, seasonCompleted ->
                    appViewModel.replaceTop(
                        Screen.GameOver(pondIndex, pads, hints, seasonCompleted)
                    )
                }
            )

            is Screen.GameOver -> GameOverScreen(
                pondIndex = current.pondIndex,
                pads = current.pads,
                hintsUsed = current.hintsUsed,
                seasonCompleted = current.unlockedSeason,
                onNextPond = { pondIndex -> appViewModel.replaceTop(Screen.Game(pondIndex)) },
                onPlayAgain = { pondIndex -> appViewModel.replaceTop(Screen.Game(pondIndex)) },
                onMenu = { appViewModel.toMenu() }
            )

            is Screen.FrogFriends -> FrogFriendsScreen(
                onBack = { appViewModel.back() }
            )
        }
    }
}
