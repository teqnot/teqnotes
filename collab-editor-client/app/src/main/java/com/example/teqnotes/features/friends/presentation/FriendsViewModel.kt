package com.example.teqnotes.features.friends.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teqnotes.features.friends.domain.model.Friend
import com.example.teqnotes.features.friends.domain.usecase.GetFriendsUseCase
import com.example.teqnotes.features.friends.domain.usecase.RemoveFriendUseCase
import com.example.teqnotes.features.friends.domain.usecase.SearchUsersUseCase
import com.example.teqnotes.features.friends.domain.usecase.SendFriendRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState: StateFlow<FriendsUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Friend>>(emptyList())
    val searchResults: StateFlow<List<Friend>> = _searchResults.asStateFlow()

    private val _pendingRequests = mutableSetOf<String>()

    init {
        loadFriends()
    }

    fun refresh() {
        loadFriends()
    }

    private fun loadFriends() {
        viewModelScope.launch {
            getFriendsUseCase()
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { friends ->
                    _uiState.update {
                        it.copy(friends = friends, isLoading = false, error = null)
                    }
                }
        }
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            searchUsersUseCase(query)
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { users ->
                    val currentFriends = _uiState.value.friends.map { it.id }
                    _searchResults.value = users.filter { it.id !in currentFriends }
                }
        }
    }

    fun sendFriendRequest(user: Friend) {
        if (user.id in _pendingRequests) return
        _pendingRequests.add(user.id)

        _searchResults.update { list ->
            list.map { if (it.id == user.id) it.copy(isPending = true) else it }
        }

        viewModelScope.launch {
            sendFriendRequestUseCase(user.email)
                .onSuccess {
                }
                .onFailure { error ->
                    _pendingRequests.remove(user.id)
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }


    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            removeFriendUseCase(friendId)
                .onSuccess {
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message) }
                }
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
        _pendingRequests.clear()
    }
}