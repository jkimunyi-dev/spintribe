// File: navigation/Screen.kt
package com.android.onboardingscreen.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object SignIn : Screen("signin")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Search : Screen("search")
    object Add : Screen("add")
    object Stories : Screen("story")
    object Profile : Screen("profile")

}