package com.example.teqnotes.core.ui.components.notecard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.theme.FiraCode

@Composable
fun NoteCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val formattedSubtitle = if (subtitle.length > 8) {
        "${subtitle.take(8)}..."
    } else {
        subtitle
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        color = Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontFamily = FiraCode,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = formattedSubtitle,
                        style = TextStyle(
                            fontFamily = FiraCode,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1
                    )
                }
                Box {
                    Icon(
                        painter = painterResource(id = R.drawable.sv_more_vert),
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { isMenuExpanded = true }
                    )

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        containerColor = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.width(150.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.sv_trash),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Удалить",
                                        style = TextStyle(
                                            fontFamily = FiraCode,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 14.sp
                                        ),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            onClick = {
                                isMenuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun NoteCardPreview() {
    NoteCard(title = "New Note", subtitle = "Lorem ipsum", onClick = { /* do nothing */ }, onDelete = { /* do nothing */ })
}