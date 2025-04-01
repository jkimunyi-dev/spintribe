package com.android.onboardingscreen.auth

sealed interface AuthResponse {
    data object Success : AuthResponse
    data class Error(val error: Exception) : AuthResponse
}