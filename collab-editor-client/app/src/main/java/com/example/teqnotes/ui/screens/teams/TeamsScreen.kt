package com.example.teqnotes.ui.screens.teams

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.teqnotes.R
import com.example.teqnotes.ui.components.CustomTextField
import com.example.teqnotes.ui.components.teamcard.NewTeamCard
import com.example.teqnotes.ui.components.teamcard.TeamCard
import com.example.teqnotes.ui.theme.Typography

@Composable
fun TeamsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Команды",
            style = Typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        CustomTextField(
            value = "",
            onValueChange = { /* TODO: handle search */ },
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
            items(3) { index ->
                TeamCard(
                    teamName = "Команда $index",
                    onClick = { /* TODO: open team */ }
                )
            }

            item {
                NewTeamCard(
                    onClick = { /* TODO: create new team */ }
                )
            }
        }
    }
}