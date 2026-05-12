package com.example.teqnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.teqnotes.navigation.AppNavGraph
import com.example.teqnotes.navigation.Screen
import com.example.teqnotes.ui.theme.TeqnotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeqnotesTheme {
                val navController = rememberNavController()

                val startDestination = determineStartDestination()

                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination
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