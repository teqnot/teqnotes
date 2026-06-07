package com.example.teqnotes.features.notifications.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.components.FriendRequestCard
import com.example.teqnotes.core.ui.components.NotificationCard
import com.example.teqnotes.core.ui.theme.FiraCode
import com.example.teqnotes.core.utils.NotificationTimeFormatter
import com.example.teqnotes.core.ui.theme.Typography
import com.example.teqnotes.features.notifications.presentation.NotificationsViewModel

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsState()
    val friendRequests by viewModel.friendRequests.collectAsState()

    val filteredNotifications = if (state.showBookmarksOnly) {
        state.notifications.filter { it.isBookmarked }
    } else {
        state.notifications
    }

    val hasFriendRequests = friendRequests.isNotEmpty()
    val hasRegularNotifications = filteredNotifications.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Уведомления",
                style = Typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                painter = painterResource(
                    id = if (state.showBookmarksOnly)
                        R.drawable.sv_bookmark_filled_large
                    else
                        R.drawable.sv_bookmark_large
                ),
                contentDescription = "Bookmarks",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { viewModel.toggleBookmarksFilter() }
                    .padding(8.dp)
            )
        }

        if (!hasFriendRequests && !hasRegularNotifications) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Уведомлений пока нет",
                    style = TextStyle(
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (hasFriendRequests) {
                    items(
                        items = friendRequests,
                        key = { it.id }
                    ) { request ->
                        FriendRequestCard(
                            senderName = request.senderName,
                            senderEmail = request.senderEmail,
                            timeAgo = NotificationTimeFormatter.format(request.timestamp),
                            onAccept = { viewModel.acceptFriendRequest(request.id) },
                            onReject = { viewModel.rejectFriendRequest(request.id) }
                        )
                    }
                }

                if (hasRegularNotifications) {

                    item {
                        Text(
                            text = "Активность",
                            style = TextStyle(
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(
                        items = filteredNotifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationCard(
                            isRead = notification.isRead,
                            isBookmarked = notification.isBookmarked,
                            userName = notification.userName,
                            noteName = notification.noteName,
                            updateTimeAgo = NotificationTimeFormatter.format(notification.timestamp),
                            changeTitle = notification.changeTitle,
                            changeSubtitle = notification.changeSubtitle,
                            onBookmark = { viewModel.toggleBookmark(notification.id) },
                            onClick = { viewModel.markAsRead(notification.id) }
                        )
                    }
                }
            }
        }
    }
}