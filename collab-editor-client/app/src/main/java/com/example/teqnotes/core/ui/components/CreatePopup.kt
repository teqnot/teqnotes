package com.example.teqnotes.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqnotes.core.ui.theme.FiraCode
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.theme.activeIndicatorColor

enum class CreationType {
    NOTE, PROJECT
}

@Composable
fun CreatePopup(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onCreate: (CreationType, String, String, String?) -> Unit,
    projects: Map<String, String> = emptyMap(),
    friends: List<String> = emptyList(),
    defaultProjectId: String? = null,
    defaultProjectName: String? = null
) {
    val animationDuration = 300

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(animationDuration)) + slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(animationDuration)
        ),
        exit = fadeOut(animationSpec = tween(animationDuration)) + slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(animationDuration)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                .clickable { onDismiss() }
                .imePadding()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Создать новую заметку",
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    var noteTitle by remember { mutableStateOf("") }
                    var titleFocused by remember { mutableStateOf(false) }

                    CustomTextFieldNoBorder(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        isFocused = titleFocused,
                        leadingIcon = R.drawable.sv_edit,
                        placeholder = "Название"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    var noteDescription by remember { mutableStateOf("") }
                    var descriptionFocused by remember { mutableStateOf(false) }

                    CustomTextFieldNoBorder(
                        value = noteDescription,
                        onValueChange = { noteDescription = it },
                        isFocused = descriptionFocused,
                        leadingIcon = R.drawable.sv_description,
                        placeholder = "Описание"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    var creationType by remember { mutableStateOf(CreationType.NOTE) }
                    var typeFocused by remember { mutableStateOf(false) }

                    CustomDropdownField(
                        label = "Заметка / Проект",
                        leadingIcon = R.drawable.sv_list,
                        isFocused = typeFocused,
                        selectedValue = creationType.name,
                        onFocusedChange = { typeFocused = it },
                        onValueSelected = { value ->
                            creationType = when (value) {
                                "Заметка" -> CreationType.NOTE
                                "Проект" -> CreationType.PROJECT
                                else -> CreationType.NOTE
                            }
                        },
                        options = listOf("Заметка", "Проект")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    var selectedValue by remember { mutableStateOf<String?>(null) }

                    if (creationType == CreationType.NOTE) {
                        var projectSelected by remember {
                            mutableStateOf<String?>(defaultProjectName ?: "Без проекта")
                        }
                        var projectFocused by remember { mutableStateOf(false) }

                        val projectOptions = if (projects.isEmpty()) {
                            listOf("Без проекта")
                        } else {
                            listOf("Без проекта") + projects.keys.toList()
                        }

                        LaunchedEffect(defaultProjectName) {
                            if (defaultProjectName != null) {
                                projectSelected = defaultProjectName
                                selectedValue = defaultProjectId
                            }
                        }

                        CustomDropdownField(
                            label = "Проект",
                            leadingIcon = R.drawable.sv_folder,
                            isFocused = projectFocused,
                            selectedValue = projectSelected ?: "Без проекта",
                            onFocusedChange = { projectFocused = it },
                            onValueSelected = { value ->
                                projectSelected = value

                                selectedValue =
                                    if (value == "Без проекта") {
                                        null
                                    } else {
                                        projects[value]
                                    }
                            },
                            options = projectOptions
                        )
                    } else {
                        var friendSelected by remember { mutableStateOf<String?>("Без друзей") }
                        var friendFocused by remember { mutableStateOf(false) }

                        val friendOptions = listOf("Без друзей") + friends

                        CustomDropdownField(
                            label = "Друзья",
                            leadingIcon = R.drawable.sv_person,
                            isFocused = friendFocused,
                            selectedValue = friendSelected ?: "Без друзей",
                            onFocusedChange = { friendFocused = it },
                            onValueSelected = { value ->
                                friendSelected = value
                                selectedValue = if (value == "Без друзей") null else value
                            },
                            options = friendOptions
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss
                        ) {
                            Text(
                                text = "Нет",
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    color = activeIndicatorColor(),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (noteTitle.isNotBlank() && noteDescription.isNotBlank()) {
                                        onCreate(
                                            creationType,
                                            noteTitle,
                                            noteDescription,
                                            selectedValue
                                        )
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Да",
                                fontFamily = FiraCode,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}