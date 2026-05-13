package com.example.teqnotes.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teqnotes.R
import com.example.teqnotes.components.CustomTextField
import com.example.teqnotes.components.NewNoteCard
import com.example.teqnotes.components.NoteCard
import com.example.teqnotes.ui.theme.Typography

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Заметки",
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

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(3) { index ->
                NoteCard(
                    title = "Курсач",
                    subtitle = "Lorem ipsum",
                    onClick = { /* TODO: open note */ }
                )
            }

            items(2) { index ->
                NoteCard(
                    title = "Пляж",
                    subtitle = "Lorem ipsum",
                    onClick = { /* TODO: open project */ }
                )
            }

            item {
                NewNoteCard(
                    onClick = { /* TODO: create new note */ }
                )
            }
        }
    }
}