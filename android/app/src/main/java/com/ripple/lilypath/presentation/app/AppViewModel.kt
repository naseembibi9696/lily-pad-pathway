package com.ripple.lilypath.presentation.app

import androidx.lifecycle.ViewModel
import com.ripple.lilypath.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = ArrayDeque<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current !is Screen.Splash) backStack.addLast(current)
        _screen.value = target
        syncBack()
    }

    fun replaceTop(target: Screen) {
        _screen.value = target
        syncBack()
    }

    fun back(): Boolean {
        val previous = backStack.removeLastOrNull() ?: return false
        _screen.value = previous
        syncBack()
        return true
    }

    fun toMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        syncBack()
    }

    private fun syncBack() {
        _canGoBack.value = backStack.isNotEmpty()
    }
}
