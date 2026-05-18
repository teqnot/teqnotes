package com.example.teqnotes.core.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

object NavigationManager {

    private const val ANIMATION_DURATION = 150

    fun getTopLevelEnterTransition(): EnterTransition {
        return fadeIn(animationSpec = tween(ANIMATION_DURATION))
    }

    fun getTopLevelExitTransition(): ExitTransition {
        return fadeOut(animationSpec = tween(ANIMATION_DURATION))
    }
}