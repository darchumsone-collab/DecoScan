package com.darchums.decoscan.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Result : Screen("result/{material}/{confidence}") {
        fun createRoute(material: String, confidence: Float) = "result/$material/$confidence"
    }
    object Profile : Screen("profile")
    object EcoTips : Screen("eco_tips")
}
