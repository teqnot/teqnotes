package com.example.teqnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.teqnotes.core.navigation.AppNavGraph
import com.example.teqnotes.core.navigation.Screen
import com.example.teqnotes.core.ui.theme.TeqnotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemIsDark = isSystemInDarkTheme()

            var isDarkTheme by rememberSaveable { mutableStateOf(systemIsDark) }

            TeqnotesTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                val startDestination = determineStartDestination()

                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme }
                )
            }
        }
    }

    private fun determineStartDestination(): String {
        // TODO: handle auth
        val hasActiveSession = true

        return if (hasActiveSession) {
            Screen.Home.route
        } else {
            Screen.Welcome.route
        }
    }
}