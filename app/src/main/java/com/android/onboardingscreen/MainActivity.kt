package com.android.onboardingscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.android.onboardingscreen.navigation.AppNavigation
import com.android.onboardingscreen.ui.theme.OnboardingScreenTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge design and make system bars draw on top of content
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            OnboardingScreenTheme {
                // Control system UI colors
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()

                SideEffect {
                    // Set status bar color to transparent with appropriate icons
                    systemUiController.setStatusBarColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )

                    // Set navigation bar color to transparent
                    systemUiController.setNavigationBarColor(
                        color = Color.Transparent,
                        darkIcons = false
                    )

                    // Set system bars behavior to show on swipe
                    // Using the correct constant from the systemUiController
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }

                AppNavigation()
            }
        }
    }
}