package com.example.teqnotes.utils

import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView

object HapticFeedback {

    @Composable
    fun performLightVibration() {
        val view = LocalView.current
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }
}