package com.example.teqnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.teqnotes.core.navigation.AppNavGraph
import com.example.teqnotes.core.navigation.Screen
import com.example.teqnotes.core.storage.TokenStorage
import com.example.teqnotes.core.ui.theme.TeqnotesTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenStorage: TokenStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplashScreen by mutableStateOf(true)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemIsDark = isSystemInDarkTheme()
            var isDarkTheme by rememberSaveable { mutableStateOf(systemIsDark) }

            val isLoggedIn by tokenStorage.isLoggedIn().collectAsStateWithLifecycle(initialValue = false)

            LaunchedEffect(isLoggedIn) {
                keepSplashScreen = false
            }

            splashScreen.setKeepOnScreenCondition { keepSplashScreen }

            TeqnotesTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                val startDestination = if (isLoggedIn) {
                    Screen.Home.route
                } else {
                    Screen.Welcome.route
                }

                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = !isDarkTheme },
                    onAuthSuccess = {
                    },
                    onLogout = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
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