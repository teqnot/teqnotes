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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teqnotes.core.ui.components.popups.DeleteConfirmationPopup
import com.example.teqnotes.core.ui.components.settings.AccountCard
import com.example.teqnotes.core.ui.components.settings.GeneralSettingsCard
import com.example.teqnotes.core.ui.components.settings.SupportCard
import com.example.teqnotes.core.ui.theme.Typography
import com.example.teqnotes.features.settings.presentation.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onAccountClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onFaqClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
            userName = state.userName,
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
            onDeleteAccountClick = { showDeleteConfirmation = true }
        )
    }

    if (showDeleteConfirmation) {
        DeleteConfirmationPopup(
            isVisible = true,
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                showDeleteConfirmation = false
                viewModel.deleteAccount()
                onDeleteAccountClick()
            }
        )
    }

    state.error?.let { error ->
        LaunchedEffect(error) {
        }
    }
}