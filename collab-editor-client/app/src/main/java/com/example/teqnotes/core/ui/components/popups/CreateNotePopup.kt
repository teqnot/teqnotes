package com.example.teqnotes.core.ui.components.popups

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.theme.FiraCode
import com.example.teqnotes.core.ui.theme.activeIndicatorColor

@Composable
fun CreateNotePopup(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    val animationDuration = 300

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(animationDuration)) +
                slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(animationDuration)
                ),
        exit = fadeOut(animationSpec = tween(animationDuration)) +
                slideOutVertically(
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
                        text = "Быстрая заметка",
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    var noteTitle by remember { mutableStateOf("") }
                    var isFocused by remember { mutableStateOf(false) }

                    CustomTextFieldNoBorder(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        isFocused = isFocused,
                        leadingIcon = R.drawable.sv_edit,
                        placeholder = "Название"
                    )

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
                                .clickable(
                                    enabled = noteTitle.isNotBlank(),
                                    onClick = { onCreate(noteTitle.trim()) }
                                ),
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

@Composable
fun CustomTextFieldNoBorder(
    value: String,
    onValueChange: (String) -> Unit,
    isFocused: Boolean,
    leadingIcon: Int,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = leadingIcon),
            contentDescription = null,
            tint = if (isFocused || value.isNotEmpty()) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            textStyle = TextStyle(
                fontFamily = FiraCode,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = if (value.isNotEmpty()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty() && !isFocused) {
                        Text(
                            text = placeholder,
                            fontFamily = FiraCode,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    innerTextField()
                }
            },
            singleLine = true
        )
    }
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}