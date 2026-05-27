package com.example.teqnotes.features.projects.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.components.CreatePopup
import com.example.teqnotes.core.ui.components.CreationType
import com.example.teqnotes.core.ui.components.CustomTextField
import com.example.teqnotes.core.ui.components.InfoTopBar
import com.example.teqnotes.core.ui.components.notecard.NewNoteCard
import com.example.teqnotes.core.ui.components.notecard.NoteCard
import com.example.teqnotes.features.home.presentation.HomeViewModel

@Composable
fun ProjectScreen(
    projectId: String,
    projectName: String,
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val projectNotes by viewModel.projectNotes.collectAsState()
    var isCreatePopupVisible by remember { mutableStateOf(false) }

    val projects by viewModel.uiState.collectAsState()
    val projectMap = projects.projects.associate {
        it.name to it.id
    }

    val currentProject = projects.projects.find { it.id == projectId }
    val currentProjectName = currentProject?.name ?: projectName

    LaunchedEffect(projectId) {
        viewModel.loadProjectNotes(projectId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        InfoTopBar(
            onBackClick = onBackClick,
            projectName = projectName,
            onUploadClick = { /* TODO: handle upload */ },
            onMoreClick = { /* TODO: handle more options */ }
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
            items(projectNotes) { note ->
                NoteCard(
                    title = note.title,
                    subtitle = "Lorem ipsum",
                    onClick = { /* TODO: open note */ }
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
                if (title.isNotBlank() && description.isNotBlank()) {
                    if (creationType == CreationType.NOTE) {
                        viewModel.createNewNote(title, description, projectId)
                    } else {
                        viewModel.createNewProject(title, description)
                    }
                }
                isCreatePopupVisible = false
            },
            projects = projectMap,
            friends = emptyList(),
            defaultProjectId = projectId,
            defaultProjectName = currentProjectName
        )
    }
}
