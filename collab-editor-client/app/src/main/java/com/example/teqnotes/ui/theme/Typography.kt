package com.example.teqnotes.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.teqnotes.R

val FiraCode = FontFamily(
    Font(R.font.fira_code_regular, FontWeight.Normal),
    Font(R.font.fira_code_medium, FontWeight.Medium),
    Font(R.font.fira_code_bold, FontWeight.Bold),
    Font(R.font.fira_code_light, FontWeight.Light),
    Font(R.font.fira_code_semibold, FontWeight.SemiBold)
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = FiraCode,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        color = Primary
    ),
    bodyLarge = TextStyle(
        fontFamily = FiraCode,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        color = Secondary
    ),
    labelMedium = TextStyle(
        fontFamily = FiraCode,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = Secondary
    )
)