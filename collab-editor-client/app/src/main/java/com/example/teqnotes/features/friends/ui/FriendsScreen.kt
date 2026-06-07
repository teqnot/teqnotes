package com.example.teqnotes.features.friends.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.components.CustomTextField
import com.example.teqnotes.core.ui.components.friendcard.FriendCard
import com.example.teqnotes.core.ui.theme.Typography
import com.example.teqnotes.features.friends.presentation.FriendsViewModel

@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()
    val searchResults = viewModel.searchResults.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val isSearching = searchQuery.isNotBlank()

    val displayedItems = if (isSearching) {
        searchResults.value
    } else {
        state.value.friends
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Друзья",
            style = Typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        CustomTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                viewModel.searchUsers(query)
            },
            leadingIcon = R.drawable.sv_search,
            placeholder = "Поиск",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(displayedItems) { friend ->
                FriendCard(
                    friendName = friend.name,
                    isSearchResult = isSearching,
                    isPending = friend.isPending,
                    onRequestClick = {
                        if (isSearching) {
                            viewModel.sendFriendRequest(friend)
                        }
                    },
                    onFriendClick = {
                        if (!isSearching) {
                            // TODO: open profile
                        }
                    },
                    onDeleteClick = {
                        if (!isSearching) {
                            viewModel.removeFriend(friend.id)
                        }
                    }
                )
            }

            if (displayedItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when {
                                isSearching -> "Ничего не найдено"
                                state.value.isLoading -> "Загрузка..."
                                else -> "Список друзей пуст"
                            },
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSearch()
            searchQuery = ""
        }
    }
}