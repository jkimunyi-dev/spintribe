package com.android.onboardingscreen.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
import com.android.onboardingscreen.components.BirthDatePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(
    onNavigateBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onSuccessfulSignUp: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val instrumentSansSemiBold = FontFamily(
        Font(R.font.instrument_sans_semibold)
    )

    if (showDatePicker) {
        BirthDatePicker(
            showDialog = showDatePicker,
            onDismiss = { showDatePicker = false },
            onDateSelected = { date -> birthDate = date }
        )
    }

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
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF173753)
            )
        }

        Text(
            text = "Sign up",
            fontSize = 48.sp,
            fontFamily = instrumentSansSemiBold,
            color = Color(0xFF173753),
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = "Create an account to continue!",
            color = Color(0xFF173753),
            modifier = Modifier.padding(top = 2.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = { newValue -> fullName = newValue },
            label = { Text("Full Name", color = Color(0xFF173753)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
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
            value = email,
            onValueChange = { newValue -> email = newValue },
            label = { Text("Email", color = Color(0xFF173753)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
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
            value = birthDate,
            onValueChange = {},
            label = { Text("Birth of date", color = Color(0xFF173753)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .heightIn(min = 64.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            readOnly = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select date",
                        tint = Color(0xFF173753)
                    )
                }
            },
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
            label = { Text("Set Password", color = Color(0xFF173753)) },
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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { newValue -> confirmPassword = newValue },
            label = { Text("Confirm Password", color = Color(0xFF173753)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .heightIn(min = 64.dp),
            shape = RoundedCornerShape(12.dp),
            textStyle = MaterialTheme.typography.bodyLarge,
            trailingIcon = {
                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                    Icon(
                        imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility",
                        tint = Color(0xFF173753)
                    )
                }
            },
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF173753),
                unfocusedBorderColor = Color(0xFF173753),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Button(
            onClick = {
                onSuccessfulSignUp()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF173753)
            ),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                "Sign Up",
                fontFamily = instrumentSansSemiBold,
                color = Color.White
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Already have an account?",
                color = Color(0xFF173753)
            )
            TextButton(onClick = onNavigateToSignIn) {
                Text(
                    "Login",
                    color = Color(0xFF173753),
                    fontFamily = instrumentSansSemiBold
                )
            }
        }
    }
}