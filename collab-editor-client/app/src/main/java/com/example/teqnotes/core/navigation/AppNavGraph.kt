package com.example.teqnotes.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.teqnotes.core.ui.components.BottomBar
import com.example.teqnotes.core.ui.components.CreateNotePopup
import com.example.teqnotes.features.auth.ui.LoginScreen
import com.example.teqnotes.features.auth.ui.RegisterScreen
import com.example.teqnotes.features.auth.ui.WelcomeScreen
import com.example.teqnotes.features.home.ui.HomeScreen
import com.example.teqnotes.features.notifications.presentation.NotificationsScreen
import com.example.teqnotes.features.projects.ui.ProjectScreen
import com.example.teqnotes.features.settings.ui.SettingsScreen
import com.example.teqnotes.features.teams.ui.TeamsScreen
import com.example.teqnotes.core.utils.HapticFeedback
import com.example.teqnotes.features.home.presentation.HomeViewModel

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Teams : Screen("teams")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
    object Project : Screen("project")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    val view = LocalView.current

    val showBottomNav = listOf(
        Screen.Home.route,
        Screen.Teams.route,
        Screen.Notifications.route,
        Screen.Settings.route
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination
    var previousRoute by remember { mutableStateOf(currentRoute) }

    var currentBottomRoute by remember { mutableStateOf(currentRoute) }
    var isPopupVisible by remember { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        if (currentRoute in showBottomNav) {
            currentBottomRoute = currentRoute
            if (!isPopupVisible) {
                previousRoute = currentRoute
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                enterTransition = { NavigationManager.getTopLevelEnterTransition() },
                exitTransition = { NavigationManager.getTopLevelExitTransition() }
            ) {
                composable(Screen.Welcome.route) {
                    WelcomeScreen(
                        onRegisterClick = { navController.navigate(Screen.Register.route) },
                        onLoginClick = { navController.navigate(Screen.Login.route) }
                    )
                }

                composable(Screen.Login.route) {
                    LoginScreen(
                        onBackClick = { navController.popBackStack() },
                        onLoginClick = { /* TODO */ }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        onBackClick = { navController.popBackStack() },
                        onRegisterClick = { /* TODO */ }
                    )
                }

                composable(Screen.Home.route) {
                    HomeScreen(
                        onProjectClick = { projectId ->
                            navController.navigate("${Screen.Project.route}/$projectId")
                        }
                    )
                }

                composable(
                    route = "${Screen.Project.route}/{projectId}",
                    arguments = listOf(
                        navArgument("projectId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val projectId = backStackEntry.arguments?.getString("projectId") ?: "default"

                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val projects by homeViewModel.uiState.collectAsState()
                    val projectName = projects.projects.find { it.id == projectId }?.name ?: "Проект $projectId"

                    ProjectScreen(
                        projectId = projectId,
                        projectName = projectName,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(Screen.Teams.route) {
                    TeamsScreen()
                }

                composable(Screen.Notifications.route) {
                    NotificationsScreen()
                }

                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
            }

            CreateNotePopup(
                isVisible = isPopupVisible,
                onDismiss = {
                    isPopupVisible = false
                    currentBottomRoute = previousRoute
                },
                onCreate = {
                    isPopupVisible = false
                    currentBottomRoute = previousRoute
                    // TODO: handle note creation
                }
            )
        }

        if (currentRoute in showBottomNav) {
            BottomBar(
                currentRoute = if (isPopupVisible) "new_note" else currentBottomRoute,
                isPopupVisible = isPopupVisible,
                onNavigate = { route ->
                    if (route == "new_note") {
                        isPopupVisible = true
                        HapticFeedback.performLightVibration(view)
                    } else {
                        isPopupVisible = false
                        handleNavigation(navController, route)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

private fun handleNavigation(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}