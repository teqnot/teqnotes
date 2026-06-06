package com.example.teqnotes.core.ui.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.teqnotes.R

@Composable
fun SupportCard(
    onFaqClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
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
                leadingIcon = R.drawable.sv_question,
                title = "FAQ",
                onClick = onFaqClick
            )

            SettingsDivider()

            SettingItem(
                leadingIcon = R.drawable.sv_person,
                title = "Выйти",
                onClick = onLogoutClick,
                iconTint = MaterialTheme.colorScheme.error,
                titleColor = MaterialTheme.colorScheme.error,
                trailingContent = {
                    androidx.compose.material3.Icon(
                        painter = painterResource(id = R.drawable.sv_chevron_forward),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )

            SettingsDivider()

            SettingItem(
                leadingIcon = R.drawable.sv_trash,
                title = "Удалить аккаунт",
                onClick = onDeleteAccountClick,
                iconTint = MaterialTheme.colorScheme.error,
                titleColor = MaterialTheme.colorScheme.error,
                trailingContent = {
                    androidx.compose.material3.Icon(
                        painter = painterResource(id = R.drawable.sv_chevron_forward),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        }
    }
}