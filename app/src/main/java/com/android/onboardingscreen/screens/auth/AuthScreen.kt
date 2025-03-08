package com.android.onboardingscreen.Auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.android.onboardingscreen.R

@Composable
fun AuthScreen(
    onNavigateToSignIn: () -> Unit,
    onNavigateToSignUp: () -> Unit,
) {
    val instrumentSansSemiBold = FontFamily(
        Font(R.font.instrument_sans_semibold)
    )

    var showAuthOptions by remember { mutableStateOf(false) }
    var offsetY by remember { mutableStateOf(0f) }
    val maxOffset = 200f
    val density = LocalDensity.current

    val buttonOffset by animateDpAsState(
        targetValue = with(density) { offsetY.coerceIn(0f, maxOffset).toDp() },
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.continue_with),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Get Started Button
        if (!showAuthOptions) {
            Button(
                onClick = { showAuthOptions = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .width(200.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    "Start Now",
                    fontFamily = instrumentSansSemiBold,
                    color = Color(0xFF173753)
                )
            }
        }

        // Auth Options Container
        if (showAuthOptions) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f) // Ensure it's above everything else
                    .background(Color.Black.copy(alpha = 0.5f)) // Dim background
                    .clickable(onClick = {
                        if (offsetY > maxOffset / 2) {
                            showAuthOptions = false
                            offsetY = 0f
                        }
                    })
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .offset(y = buttonOffset)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            offsetY = (offsetY + delta).coerceIn(0f, maxOffset)
                        },
                        onDragStopped = {
                            if (offsetY > maxOffset / 2) {
                                showAuthOptions = false
                                offsetY = 0f
                            } else {
                                offsetY = 0f
                            }
                        }
                    )
                    .padding(top = 16.dp, start = 40.dp, end = 40.dp, bottom = 16.dp)
                    .zIndex(2f), // Ensure it's above the dimming layer
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(5.dp)
                        .background(Color(0xFFD1D1D1), RoundedCornerShape(2.5.dp))
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Continue with Apple Button
                Button(
                    onClick = { /* TODO: Implement Apple Sign In */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF173753)
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.apple),
                            contentDescription = "Apple icon",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Continue with Apple",
                            fontFamily = instrumentSansSemiBold,
                            color = Color.White
                        )
                    }
                }

                // Continue with Google Button
                Button(
                    onClick = { /* TODO: Implement Google Sign In */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5E5E5)
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google icon",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Text(
                            "Continue with Google",
                            fontFamily = instrumentSansSemiBold,
                            color = Color(0xFF173753)
                        )
                    }
                }

                // Sign up with email Button
                Button(
                    onClick = onNavigateToSignUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5E5E5)
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.email),
                            contentDescription = "Email icon",
                            tint = Color(0xFF173753),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Sign up with email",
                            fontFamily = instrumentSansSemiBold,
                            color = Color(0xFF173753)
                        )
                    }
                }

                // Login Button
                Button(
                    onClick = onNavigateToSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFF173753),
                            shape = RoundedCornerShape(50.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        "Login",
                        fontFamily = instrumentSansSemiBold,
                        color = Color(0xFF173753)
                    )
                }
            }
        }
    }
}