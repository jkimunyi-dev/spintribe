package com.android.onboardingscreen.navigation

import NotificationsScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.android.onboardingscreen.Auth.AuthScreen
import com.android.onboardingscreen.screens.onBoarding.OnBoarding
import com.android.onboardingscreen.screens.auth.SignIn
import com.android.onboardingscreen.screens.auth.SignUp
import com.android.onboardingscreen.screens.home.HomeScreen
import com.android.onboardingscreen.screens.profile.ProfileScreen
import com.android.onboardingscreen.screens.events.EventsScreen
import com.android.onboardingscreen.auth.AuthenticationManager
import com.android.onboardingscreen.data.DataStoreManager
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.android.onboardingscreen.screens.loading.LoadingScreen
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.delay


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authManager = remember { AuthenticationManager(context) }
    val dataStoreManager = remember { DataStoreManager(context) }
    
    var isLoading by remember { mutableStateOf(true) }
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        delay(1500) // Add a small delay for smoother transition
        val hasCompletedOnboarding = dataStoreManager.hasCompletedOnboarding().first()
        val isSignedIn = authManager.isUserSignedIn()
        
        startDestination = when {
            hasCompletedOnboarding && isSignedIn -> Screen.Home.route
            hasCompletedOnboarding -> Screen.Auth.route
            else -> Screen.Onboarding.route
        }
        isLoading = false
    }

    if (isLoading || startDestination == null) {
        LoadingScreen(
            onLoadingComplete = { destination ->
                startDestination = destination
                isLoading = false
            }
        )
        return
    }

    NavHost(
        navController = navController,
        startDestination = startDestination!!
    ) {
        composable(route = Screen.Onboarding.route) {
            OnBoarding(
                onNavigateToAuth = {
                    scope.launch {
                        dataStoreManager.setOnboardingCompleted()
                    }
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Auth.route) {
            AuthScreen(
                onNavigateToSignIn = { navController.navigate(Screen.SignIn.route) },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) }
            )
        }

        composable(route = Screen.SignIn.route) {
            SignIn(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onSuccessfulSignIn = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.SignUp.route) {
            SignUp(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onSuccessfulSignUp = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Home.route) {
            Scaffold(
                bottomBar = {
                    BottomNavigation(
                        navController = navController,
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            ) { paddingValues ->
                HomeScreen(Modifier.padding(paddingValues))
            }
        }

        composable(route = Screen.MyEvents.route) {
            Scaffold(
                bottomBar = {
                    BottomNavigation(
                        navController = navController,
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            ) { paddingValues ->
                EventsScreen(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }

        composable(route = Screen.Notifications.route) {
            Scaffold(
                bottomBar = {
                    BottomNavigation(
                        navController = navController,
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            ) { _ ->
                NotificationsScreen()
            }
        }

        composable(route = Screen.Profile.route) {
            Scaffold(
                bottomBar = {
                    BottomNavigation(
                        navController = navController,
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            ) { _ ->  // Changed paddingValues to _
                ProfileScreen(navController)
            }
        }
    }
}

@Composable
private fun shouldShowBottomBar(navController: NavController): Boolean {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    return when (currentRoute) {
        Screen.Home.route, Screen.MyEvents.route, 
        Screen.Notifications.route, Screen.Profile.route -> true
        else -> false
    }
}

// The NotificationsScreen is now imported from the notifications package
// @Composable
// fun NotificationsScreen() {
//     Box(
//         modifier = Modifier.fillMaxSize(),
//         contentAlignment = Alignment.Center
//     ) {
//         Text("Notifications Screen")
//     }
// } is removed
