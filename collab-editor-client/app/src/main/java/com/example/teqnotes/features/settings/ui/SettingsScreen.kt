package com.example.teqnotes.features.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.teqnotes.core.ui.components.settings.AccountCard
import com.example.teqnotes.core.ui.components.settings.GeneralSettingsCard
import com.example.teqnotes.core.ui.components.settings.SupportCard
import com.example.teqnotes.core.ui.theme.Typography

@Composable
fun SettingsScreen(
    onAccountClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Настройки",
            style = Typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        AccountCard(
            userName = "John Doe", // TODO: получить из ViewModel
            onClick = onAccountClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        GeneralSettingsCard(
            onSecurityClick = onSecurityClick,
            onNotificationsClick = onNotificationsClick,
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle
        )

        Spacer(modifier = Modifier.height(16.dp))

        SupportCard(
            onFaqClick = onFaqClick,
            onLogoutClick = onLogoutClick,
            onDeleteAccountClick = onDeleteAccountClick
        )
    }
}