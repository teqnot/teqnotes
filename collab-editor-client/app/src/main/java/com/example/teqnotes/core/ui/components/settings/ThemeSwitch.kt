package com.example.teqnotes.core.ui.components.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.teqnotes.core.ui.theme.activeIndicatorColor

@Composable
fun ThemeSwitch(
    isDarkTheme: Boolean,
    onToggle: () -> Unit
) {
    val offsetX by animateDpAsState(
        targetValue = if (isDarkTheme) 24.dp else 0.dp,
        label = "Switch Offset"
    )

    Box(
        modifier = Modifier
            .size(width = 50.dp, height = 24.dp)
            .background(
                color = activeIndicatorColor(),
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .offset(x = offsetX)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}