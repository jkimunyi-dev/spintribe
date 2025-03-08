package com.android.onboardingscreen.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.onboardingscreen.Auth.AuthScreen
import com.android.onboardingscreen.screens.onBoarding.OnBoarding
import com.android.onboardingscreen.screens.auth.SignIn
import com.android.onboardingscreen.screens.auth.SignUp
import com.android.onboardingscreen.screens.home.HomeScreen
import com.android.onboardingscreen.screens.profile.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(navController)) {
                BottomNavigation(navController = navController)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Onboarding.route
            ) {
                composable(route = Screen.Onboarding.route) {
                    OnBoarding(
                        onNavigateToAuth = {
                            navController.navigate(Screen.Auth.route) {
                                popUpTo(Screen.Onboarding.route) {
                                    inclusive = true
                                }
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
                                popUpTo(Screen.Onboarding.route) {
                                    inclusive = true
                                }
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
                                popUpTo(Screen.Onboarding.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                // Add new screens
                composable(route = Screen.Home.route) {
                    HomeScreen()
                }

                composable(route = Screen.Search.route) {
                    SearchScreen()
                }

                composable(route = Screen.Add.route) {
                    AddScreen()
                }

                composable(route = Screen.Stories.route) {
                    StoriesScreen()
                }

                composable(route = Screen.Profile.route) {
                    ProfileScreen()
                }
            }
        }
    }
}

@Composable
private fun shouldShowBottomBar(navController: androidx.navigation.NavController): Boolean {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    return when (currentRoute) {
        Screen.Home.route, Screen.Search.route, Screen.Add.route,
        Screen.Stories.route, Screen.Profile.route -> true
        else -> false
    }
}

// Placeholder screens
@Composable
fun SearchScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        androidx.compose.material3.Text("Search Screen")
    }
}

@Composable
fun AddScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        androidx.compose.material3.Text("Add Screen")
    }
}

@Composable
fun StoriesScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        androidx.compose.material3.Text("Stories Screen")
    }
}

//@Composable
//fun ProfileScreen() {
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
//        androidx.compose.material3.Text("Profile Screen")
//    }
//}
