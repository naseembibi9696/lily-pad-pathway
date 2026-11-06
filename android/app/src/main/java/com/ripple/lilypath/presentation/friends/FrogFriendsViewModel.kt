package com.ripple.lilypath.presentation.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ripple.lilypath.data.sample.SampleData
import com.ripple.lilypath.domain.model.FrogFriend
import com.ripple.lilypath.domain.repository.ProgressRepository
import com.ripple.lilypath.domain.usecase.UnlockSeasonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FrogFriendsUiState(
    val loading: Boolean = true,
    val friends: List<FrogFriend> = emptyList()
) {
    val unlockedCount: Int = friends.count { it.unlocked }

    val empty: Boolean = !loading && unlockedCount == 0
}

class FrogFriendsViewModel(
    private val progress: ProgressRepository,
    private val unlockSeason: UnlockSeasonUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FrogFriendsUiState())
    val state: StateFlow<FrogFriendsUiState> = _state.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val completed = unlockSeason.completed(progress.solvedPonds())
            _state.value = FrogFriendsUiState(
                loading = false,
                friends = SampleData.frogFriends.map { friend ->
                    friend.copy(unlocked = completed.contains(friend.season))
                }
            )
        }
    }
}
