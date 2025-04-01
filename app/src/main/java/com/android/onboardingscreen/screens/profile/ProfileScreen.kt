// File: screens/profile/ProfileScreen.kt
package com.android.onboardingscreen.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.onboardingscreen.R
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.android.onboardingscreen.auth.AuthenticationManager
import com.android.onboardingscreen.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val authManager = remember { AuthenticationManager(context) }
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }
    val userEmail = currentUser?.email ?: "No email"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Username (Email)
        Text(
            text = userEmail,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign out button
        Button(
            onClick = {
                authManager.signOut()
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        ) {
            Text("Sign Out")
        }

        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("Posts", "Achievements", "Friends")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with Edit and More Options
                item {
                    TopAppBar(
                        title = { },
                        actions = {
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile"
                                )
                            }
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More Options"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        )
                    )
                }

                // Profile Section
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Username
                        Text(
                            text = "@baraka",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // Tabs
                item {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = Color(0xFF173753),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 2.dp,
                                color = Color(0xFF173753)
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(text = title) }
                            )
                        }
                    }
                }

                // Content based on selected tab
                item {
                    when (selectedTab) {
                        0 -> PostsGrid()
                        1 -> AchievementsGrid()
                        2 -> FriendsGrid()
                    }
                }

                // Add space at the bottom for better scrolling experience
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun PostsGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(400.dp) // Set a fixed height for the grid
    ) {
        items(6) { index ->
            val imageRes = when (index % 3) {
                0 -> R.drawable.im1
                1 -> R.drawable.im22
                else -> R.drawable.im3
            }
            GridImage(imageRes = imageRes)
        }
    }
}

@Composable
fun AchievementsGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Match height with other tabs
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.trophy),
            contentDescription = "Achievements",
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Achievements Coming Soon",
            fontSize = 18.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun FriendsGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp) // Match height with other tabs
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Friends",
            tint = Color(0xFF173753),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Friends Coming Soon",
            fontSize = 18.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun GridImage(modifier: Modifier = Modifier, imageRes: Int) {
    Card(
        modifier = modifier
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Grid Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
