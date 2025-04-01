// File: navigation/BottomNavigation.kt
package com.android.onboardingscreen.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.android.onboardingscreen.R

@Composable
fun BottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavigationItem(
            route = Screen.Home.route,
            icon = R.drawable.home,
            label = "Home"
        ),
        NavigationItem(
            route = Screen.MyEvents.route,
            icon = R.drawable.event,
            label = "My Events"
        ),
        NavigationItem(
            route = Screen.Notifications.route,
            icon = R.drawable.notification,
            label = "Notifications"
        ),
        NavigationItem(
            route = Screen.Profile.route,
            icon = R.drawable.profile,
            label = "Profile"
        )
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val defaultIconSize = 24.dp

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(top = 3.dp, bottom = 3.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp // Add elevation to make it stand out
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Color(0xFF173753) else Color(0xFF173753).copy(alpha = 0.8f),
                        modifier = Modifier.size(defaultIconSize)
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF173753),
                    unselectedIconColor = Color(0xFF173753).copy(alpha = 0.8f),
                    indicatorColor = Color.White
                )
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val icon: Int,
    val label: String
)
