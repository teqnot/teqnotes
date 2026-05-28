package com.example.teqnotes.features.note.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teqnotes.core.ui.components.bars.InfoTopBar
import com.example.teqnotes.core.ui.theme.FiraCode
import com.example.teqnotes.features.note.presentation.NoteEditorViewModel

@Composable
fun NoteEditorScreen(
    noteId: String,
    projectName: String,
    onBackClick: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(state.note) {
        state.note?.let { note ->
            title = note.title
            content = note.content
        }
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
            projectName = projectName, // TODO: получить реальное название
            onUploadClick = { /* TODO */ },
            onMoreClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = title,
            onValueChange = { newTitle ->
                title = newTitle
                viewModel.updateNoteTitle(newTitle)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textStyle = TextStyle(
                fontFamily = FiraCode,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.secondary),
            decorationBox = { innerTextField ->
                Box {
                    if (title.isEmpty()) {
                        Text(
                            text = "Название заметки",
                            style = TextStyle(
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 32.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )

        BasicTextField(
            value = content,
            onValueChange = { newContent ->
                content = newContent
                viewModel.updateNoteContent(newContent)
            },
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            textStyle = TextStyle(
                fontFamily = FiraCode,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.secondary),
            decorationBox = { innerTextField ->
                Box {
                    if (content.isEmpty()) {
                        Text(
                            text = "...",
                            style = TextStyle(
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}