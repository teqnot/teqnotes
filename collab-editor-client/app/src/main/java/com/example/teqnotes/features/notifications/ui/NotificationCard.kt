package com.example.teqnotes.features.notifications.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.theme.FiraCode
import com.example.teqnotes.core.ui.theme.activeIndicatorColor
import com.example.teqnotes.core.utils.HapticFeedback
import kotlinx.coroutines.launch


@Composable
fun NotificationCard(
    isRead: Boolean,
    isBookmarked: Boolean,
    userName: String,
    noteName: String,
    updateTimeAgo: String,
    changeTitle: String,
    changeSubtitle: String,
    onBookmark: () -> Unit,
    onClick: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }

    var hasVibratedThisDrag by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val view = LocalView.current

    val maxSwipe = -240f
    val saveThreshold = -220f
    val cardHeight = 110.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        HapticFeedback.performSaveVibrationStart(view)
                    },

                    onHorizontalDrag = { _, dragAmount ->
                        val newOffset = (offsetX.value + dragAmount).coerceIn(maxSwipe, 0f)

                        scope.launch {
                            offsetX.snapTo(newOffset)
                        }

                        if (newOffset <= saveThreshold && !hasVibratedThisDrag) {
                            HapticFeedback.performSaveVibrationThreshold(view)
                            hasVibratedThisDrag = true
                            onBookmark()
                        }

                        if (newOffset > saveThreshold) {
                            hasVibratedThisDrag = false
                        }
                    },

                    onDragEnd = {
                        scope.launch {
                            offsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(250)
                            )
                        }
                        hasVibratedThisDrag = false
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = activeIndicatorColor(),
                    shape = RoundedCornerShape(20.dp)
                )
                .height(cardHeight)
                .zIndex(0f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sv_bookmark_filled_large),
                            contentDescription = "Save",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Save",
                        style = TextStyle(
                            fontFamily = FiraCode,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .offset { IntOffset(offsetX.value.toInt(), 0) }
                .zIndex(1f)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isBookmarked) {
                        Icon(
                            painter = painterResource(id = R.drawable.sv_bookmark_filled_tiny),
                            contentDescription = "Bookmarked",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(end = 8.dp)
                        )
                    } else if (!isRead) {
                        Icon(
                            painter = painterResource(id = R.drawable.sv_new_notif),
                            contentDescription = "New notification",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(end = 8.dp)
                        )
                    }

                    Text(
                        text = "$userName / $noteName",
                        style = TextStyle(
                            fontFamily = FiraCode,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = updateTimeAgo,
                        style = TextStyle(
                            fontFamily = FiraCode,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = changeTitle,
                    style = TextStyle(
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = changeSubtitle,
                    style = TextStyle(
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}