package com.android.onboardingscreen.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.onboardingscreen.R
import com.android.onboardingscreen.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignIn(
    onNavigateBack: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onSuccessfulSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val instrumentSansSemiBold = FontFamily(
        Font(R.font.instrument_sans_semibold)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled. ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF173753)
            )
        }

        Text(
            text = "Sign in to your\nAccount",
            fontSize = 32.sp,
            fontFamily = instrumentSansSemiBold,
            color = Color(0xFF173753),
            modifier = Modifier.padding(top = 24.dp),
            lineHeight = 38.sp
        )

        Text(
            text = "Enter your email and password to log in",
            color = Color(0xFF173753),
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { newValue -> email = newValue },
            label = { Text("Email", color = Color(0xFF173753)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .heightIn(min = 64.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF173753),
                unfocusedBorderColor = Color(0xFF173753),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { newValue -> password = newValue },
            label = { Text("Password", color = Color(0xFF173753)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .heightIn(min = 64.dp),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility",
                        tint = Color(0xFF173753)
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF173753),
                unfocusedBorderColor = Color(0xFF173753),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        TextButton(
            onClick = { /* TODO: Implement forgot password */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                "Forgot Password?",
                color = Color(0xFF173753)
            )
        }

        Button(
            onClick = {
                // After successful authentication
                onSuccessfulSignIn()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF173753)
            )
        ) {
            Text("Log In", fontFamily = instrumentSansSemiBold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Don't have an account?",
                color = Color(0xFF173753)
            )
            TextButton(onClick = onNavigateToSignUp) {
                Text(
                    "Sign Up",
                    color = Color(0xFF173753),
                    fontFamily = instrumentSansSemiBold
                )
            }
        }
    }
}