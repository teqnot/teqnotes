package com.example.teqnotes.features.friends.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.friends.domain.usecase.AddFriendUseCase
import com.example.teqnotes.features.friends.domain.usecase.GetFriendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val addFriendUseCase: AddFriendUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadFriends()
    }

    private fun loadFriends() {
        viewModelScope.launch {
            getFriendsUseCase().collect { friends ->
                _uiState.value = _uiState.value.copy(friends = friends)
            }
        }

        if (_uiState.value.friends.isEmpty()) {
            loadMockData()
        }
    }

    private fun loadMockData() {
        viewModelScope.launch {
            addFriendUseCase(
                Friend(
                    id = "friend_1",
                    name = "Иван Иванов",
                    email = "ivan@example.com",
                    createdAt = System.currentTimeMillis()
                )
            )
            addFriendUseCase(
                Friend(
                    id = "friend_2",
                    name = "Мария Петрова",
                    email = "maria@example.com",
                    createdAt = System.currentTimeMillis() - 86400000
                )
            )
            addFriendUseCase(
                Friend(
                    id = "friend_3",
                    name = "Алексей Сидоров",
                    email = "alex@example.com",
                    createdAt = System.currentTimeMillis() - 172800000
                )
            )
        }
    }

    fun addNewFriend(name: String, email: String) {
        viewModelScope.launch {
            addFriendUseCase(
                Friend(
                    id = "friend_${System.currentTimeMillis()}",
                    name = name,
                    email = email,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}