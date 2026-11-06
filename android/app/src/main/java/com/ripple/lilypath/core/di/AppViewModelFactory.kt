package com.ripple.lilypath.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ripple.lilypath.presentation.app.AppViewModel
import com.ripple.lilypath.presentation.friends.FrogFriendsViewModel
import com.ripple.lilypath.presentation.game.GameViewModel
import com.ripple.lilypath.presentation.gameover.GameOverViewModel
import com.ripple.lilypath.presentation.menu.MenuViewModel
import com.ripple.lilypath.presentation.splash.SplashViewModel

object AppViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                ServiceLocator.progress,
                ServiceLocator.settings,
                ServiceLocator.unlockSeason
            )
            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                ServiceLocator.progress,
                ServiceLocator.settings,
                ServiceLocator.generatePond,
                ServiceLocator.validateMove,
                ServiceLocator.nextHint,
                ServiceLocator.unlockSeason,
                ServiceLocator.feedback
            )
            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                ServiceLocator.progress,
                ServiceLocator.generatePond,
                ServiceLocator.unlockSeason
            )
            modelClass.isAssignableFrom(FrogFriendsViewModel::class.java) -> FrogFriendsViewModel(
                ServiceLocator.progress,
                ServiceLocator.unlockSeason
            )
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
