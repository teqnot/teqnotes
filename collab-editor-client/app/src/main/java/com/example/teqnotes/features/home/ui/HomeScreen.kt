package com.example.teqnotes.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.components.CreatePopup
import com.example.teqnotes.core.ui.components.CreationType
import com.example.teqnotes.core.ui.components.CustomTextField
import com.example.teqnotes.core.ui.components.notecard.NewNoteCard
import com.example.teqnotes.core.ui.components.notecard.NoteCard
import com.example.teqnotes.core.ui.components.ProjectCard
import com.example.teqnotes.core.ui.theme.Typography
import com.example.teqnotes.features.home.presentation.HomeViewModel

@Composable
fun HomeScreen(
    onProjectClick: (String) -> Unit,
    onNoteClick: (String, String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()
    var isCreatePopupVisible by remember { mutableStateOf(false) }

    val projectMap = state.value.projects.associate {
        it.name to it.id
    }
    val friendNames = listOf("Иван", "Мария", "Алексей") // TODO: handle friends

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
            items(state.value.individualNotes) { note ->
                NoteCard(
                    title = note.title,
                    subtitle = "Lorem ipsum",
                    onClick = { onNoteClick(note.id, "__individual__") }
                )
            }

            items(state.value.projects) { project ->
                ProjectCard(
                    title = project.name,
                    subtitle = "Lorem ipsum",
                    onClick = {
                        onProjectClick(project.id)
                    }
                )
            }

            item {
                NewNoteCard(
                    onClick = { isCreatePopupVisible = true }
                )
            }
        }
    }

    if (isCreatePopupVisible) {
        CreatePopup(
            isVisible = true,
            onDismiss = { isCreatePopupVisible = false },
            onCreate = { creationType, title, description, extra ->

                when (creationType) {
                    CreationType.NOTE -> {
                        viewModel.createNewNote(
                            title,
                            description,
                            extra
                        )
                    }

                    CreationType.PROJECT -> {
                        viewModel.createNewProject(
                            title,
                            description
                        )
                    }
                }

                isCreatePopupVisible = false
            },
            projects = projectMap,
            friends = friendNames
        )
    }
}