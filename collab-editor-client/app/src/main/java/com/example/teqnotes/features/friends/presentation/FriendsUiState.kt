package com.example.teqnotes.features.friends.presentation

import com.example.teqnotes.features.friends.domain.model.Friend

data class FriendsUiState(
    val friends: List<Friend> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)