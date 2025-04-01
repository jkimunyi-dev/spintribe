// File: navigation/Screen.kt
package com.android.onboardingscreen.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object MyEvents : Screen("myevents")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")

}
