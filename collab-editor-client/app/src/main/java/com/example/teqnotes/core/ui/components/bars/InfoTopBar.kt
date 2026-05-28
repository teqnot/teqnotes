package com.example.teqnotes.core.ui.components.bars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun InfoTopBar(
    onBackClick: () -> Unit,
    projectName: String,
    onUploadClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.sv_arrow_backward),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onBackClick)
                    .padding(8.dp)
            )

            Text(
                text = projectName,
                fontFamily = FiraCode,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.sv_upload),
                contentDescription = "Upload",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onUploadClick)
                    .padding(8.dp)
            )

            Icon(
                painter = painterResource(id = R.drawable.sv_more_horiz),
                contentDescription = "More options",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onMoreClick)
                    .padding(8.dp)
            )
        }
    }
}