package com.jionifamily.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jionifamily.presentation.child.home.ChildHomeScreen
import com.jionifamily.presentation.login.LoginScreen
import com.jionifamily.presentation.parent.home.ParentHomeScreen
import com.jionifamily.presentation.parent.mission.MissionCreateScreen
import com.jionifamily.presentation.shared.history.HistoryScreen
import com.jionifamily.presentation.shared.settings.SettingsScreen
import com.jionifamily.presentation.splash.SplashScreen

@Composable
fun JioniNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToParentHome = {
                    navController.navigate(Screen.ParentHome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToChildHome = {
                    navController.navigate(Screen.ChildHome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    val dest = if (role == "parent") Screen.ParentHome.route else Screen.ChildHome.route
                    navController.navigate(dest) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
            )
        }

        composable(Screen.ParentHome.route) {
            ParentHomeScreen(
                onNavigateToCreateMission = {
                    navController.navigate(Screen.MissionCreate.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.MissionHistory.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
            )
        }

        composable(Screen.ChildHome.route) {
            ChildHomeScreen(
                onNavigateToHistory = {
                    navController.navigate(Screen.MissionHistory.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
            )
        }

        composable(Screen.MissionHistory.route) {
            HistoryScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.MissionCreate.route) {
            MissionCreateScreen(
                onBack = { navController.popBackStack() },
                onMissionCreated = { navController.popBackStack() },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
