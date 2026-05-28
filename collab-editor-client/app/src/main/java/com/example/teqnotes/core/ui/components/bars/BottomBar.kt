package com.example.teqnotes.core.ui.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.theme.activeIndicatorColor

enum class BottomNavItem(
    val route: String,
    val icon: Int,
    val title: String
) {
    HOME("home", R.drawable.sv_home, "Заметки"),
    FRIENDS("friends", R.drawable.sv_groups, "Друзья"),
    NEW_NOTE("new_note", R.drawable.sv_new_note, "Новая заметка"),
    NOTIFICATIONS("notifications", R.drawable.sv_notifications, "Уведомления"),
    SETTINGS("settings", R.drawable.sv_settings, "Настройки")
}

@Composable
fun BottomBar(
    currentRoute: String,
    isPopupVisible: Boolean,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface,
                RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem.entries.forEach { item ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(2.dp)
                    .clickable {
                        onNavigate(item.route)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (currentRoute == item.route || (item.route == "new_note" && isPopupVisible)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(activeIndicatorColor(),
                                RoundedCornerShape(20.dp))
                    )
                }

                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}