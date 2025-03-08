// Theme.kt
package com.android.onboardingscreen.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF173753),
    background = Color.White,
    surface = Color.White,
    onBackground = Color(0xFF173753),
    onSurface = Color(0xFF173753),
    onPrimary = Color.White
)

@Composable
fun OnboardingScreenTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}