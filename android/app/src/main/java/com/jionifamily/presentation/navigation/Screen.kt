package com.jionifamily.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object ParentHome : Screen("parent_home")
    object ChildHome : Screen("child_home")
    object MissionCreate : Screen("mission_create")
    object MissionDetail : Screen("mission_detail/{missionId}") {
        fun createRoute(missionId: String) = "mission_detail/$missionId"
    }
    object MissionHistory : Screen("mission_history")
    object Settings : Screen("settings")
}
