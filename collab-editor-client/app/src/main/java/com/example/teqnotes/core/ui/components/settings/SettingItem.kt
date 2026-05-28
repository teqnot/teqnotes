package com.example.teqnotes.core.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teqnotes.R
import com.example.teqnotes.core.ui.theme.FiraCode

@Composable
fun SettingItem(
    leadingIcon: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: androidx.compose.ui.graphics.Color? = null,
    titleColor: androidx.compose.ui.graphics.Color? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = leadingIcon),
            contentDescription = null,
            tint = iconTint ?: MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(end = 16.dp)
        )

        Text(
            text = title,
            style = androidx.compose.ui.text.TextStyle(
                fontFamily = FiraCode,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            ),
            color = titleColor ?: MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                painter = painterResource(id = R.drawable.sv_chevron_forward),
                contentDescription = null,
                tint = iconTint ?: MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}