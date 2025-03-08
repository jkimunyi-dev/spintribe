// File: navigation/BottomNavigation.kt
package com.android.onboardingscreen.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
            icon = R.drawable.home
        ),
//        NavigationItem(
//            route = Screen.Search.route,
//            icon = R.drawable.search
//        ),
        NavigationItem(
            route = Screen.Add.route,
            icon = R.drawable.add
        ),
        NavigationItem(
            route = Screen.Stories.route,
            icon = R.drawable.story
        ),
        NavigationItem(
            route = Screen.Profile.route,
            icon = R.drawable.profile
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Get the default icon size from the theme
    val defaultIconSize = 30.dp  // This is typically the default icon size in Material Design

    // Double the size for our icons
//    val doubledIconSize = defaultIconSize * 2

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        containerColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.route,
                        tint = Color(0xFF173753),
                        modifier = Modifier
                            .size(defaultIconSize)  // Double the icon size
//                            .padding(1.dp)  // Add 3dp padding around the icon
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
    val icon: Int
)