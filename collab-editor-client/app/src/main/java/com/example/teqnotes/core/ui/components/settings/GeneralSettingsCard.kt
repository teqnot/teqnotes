package com.example.teqnotes.core.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.teqnotes.R

@Composable
fun GeneralSettingsCard(
    onSecurityClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column {
            SettingItem(
                leadingIcon = R.drawable.sv_lock,
                title = "Безопасность",
                onClick = onSecurityClick
            )

            SettingsDivider()

            SettingItem(
                leadingIcon = R.drawable.sv_notifications,
                title = "Уведомления",
                onClick = onNotificationsClick
            )

            SettingsDivider()

            SettingItem(
                leadingIcon = R.drawable.sv_moon,
                title = "Темная тема",
                onClick = {},
                trailingContent = {
                    ThemeSwitch(
                        isDarkTheme = isDarkTheme,
                        onToggle = onThemeToggle
                    )
                }
            )
        }
    }
}