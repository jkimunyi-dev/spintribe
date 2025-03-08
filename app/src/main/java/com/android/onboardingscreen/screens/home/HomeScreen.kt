// screens/home/HomeScreen.kt
package com.android.onboardingscreen.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.onboardingscreen.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val instrumentSans = FontFamily(
        Font(R.font.instrument_sans_semibold, FontWeight.SemiBold)
    )
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Upcoming", "Past")

    // Adventure theme colors
    val primaryGreen = Color(0xFF1E7A56)
    val darkBrown = Color(0xFF3E2723)
    val lightBrown = Color(0xFF8D6E63)
    val skyBlue = Color(0xFF64B5F6)
    val sunsetOrange = Color(0xFFFF7043)

    val featuredEvents = remember {
        listOf(
            FeaturedEventData(
                "Camp Carnelley's",
                "16 Mar 2025",
                "Lower Kwa Muhia",
                "Circular",
                "10 km/h",
                "32.3 km",
                Color(0xFF1F2937)
            ),
            FeaturedEventData(
                "Mt. Longonot Hike",
                "22 Apr 2025",
                "Naivasha Town",
                "Linear",
                "5 km/h",
                "45.0 km",
                darkBrown
            ),
            FeaturedEventData(
                "Lake Nakuru Safari",
                "05 May 2025",
                "Nakuru Gate",
                "Circular",
                "8 km/h",
                "28.7 km",
                primaryGreen.copy(alpha = 0.8f)
            )
        )
    }

    val allEvents = remember {
        listOf(
            EventData("Camp Carnelley's", "16 Mar 2025", "32.3 km", EventStatus.OPEN),
            EventData("Lake Naivasha Resort", "22 Apr 2025", "45.0 km", EventStatus.OPEN),
            EventData("Hell's Gate National Park", "05 May 2025", "28.7 km", EventStatus.OPEN),
            EventData("Mt. Longonot Hike", "18 Jun 2025", "56.2 km", EventStatus.OPEN),
            EventData("Nairobi National Park", "10 Jan 2025", "15.4 km", EventStatus.CLOSED)
        )
    }

    // Filter events based on search query and selected tab
    val filteredEvents = remember(searchQuery, selectedTab) {
        allEvents.filter { event ->
            val matchesSearch = event.name.contains(searchQuery, ignoreCase = true)
            val matchesTab = when (selectedTab) {
                0 -> true // All events
                1 -> event.status == EventStatus.OPEN // Upcoming
                2 -> event.status == EventStatus.CLOSED // Past
                else -> true
            }
            matchesSearch && matchesTab
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Adjust to minimize space at top
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 0.dp)
        ) {
            // Top Bar - no top padding
            item {
                TopAppBar(
                    title = {
                        Text(
                            "For You",
                            fontFamily = instrumentSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = darkBrown
                        )
                    },
                    actions = {
                        Box(contentAlignment = Alignment.Center) {
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = darkBrown
                                )
                            }
                            // Green notification indicator (adjusted position)
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(primaryGreen, CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-6).dp, y = 6.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    windowInsets = WindowInsets(0, 0, 0, 0)
                )
            }

            // Featured event carousel
            item {
                Text(
                    text = "Featured Adventures",
                    fontFamily = instrumentSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = darkBrown,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(featuredEvents) { event ->
                        FeaturedEventCard(event)
                    }
                }
            }

            // Search bar with 100% rounded corners
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    placeholder = { Text("Search Adventure...", color = lightBrown) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = primaryGreen
                        )
                    },
                    shape = CircleShape,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = lightBrown,
                        focusedBorderColor = primaryGreen
                    ),
                    singleLine = true
                )
            }

            // Scrollable tab row
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    edgePadding = 0.dp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(primaryGreen)
                            )
                        }
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) primaryGreen else lightBrown
                                )
                            }
                        )
                    }
                }
            }

            // List of filtered events
            items(filteredEvents) { event ->
                EventListItem(event, primaryGreen, darkBrown, lightBrown)
            }

            // Add empty space at the bottom for better scrolling experience
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

data class FeaturedEventData(
    val name: String,
    val date: String,
    val startLocation: String,
    val trackType: String,
    val averageSpeed: String,
    val distance: String,
    val backgroundColor: Color
)

data class EventData(
    val name: String,
    val date: String,
    val distance: String,
    val status: EventStatus
)

enum class EventStatus {
    OPEN,
    CLOSED
}

@Composable
fun FeaturedEventCard(event: FeaturedEventData) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(190.dp),  // Increased height to 190dp
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = event.backgroundColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = event.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_icon),
                        contentDescription = "Date",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Date: ${event.date}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.location_icon),
                        contentDescription = "Location",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Start: ${event.startLocation}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.track_icon),
                        contentDescription = "Track",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Track Type: ${event.trackType}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.speed_icon),
                        contentDescription = "Speed",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Avg Speed: ${event.averageSpeed}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
            }

            Row(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF4CAF50), CircleShape)
                        .size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.location_pin),
                        contentDescription = "Location Pin",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = event.distance,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(48.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.trophy),
                    contentDescription = "Trophy",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(32.dp)
                )
            }

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    text = "More...",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun EventListItem(event: EventData, primaryGreen: Color, darkBrown: Color, lightBrown: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)  // Increased height by 10dp
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = event.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBrown
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar_icon),
                        contentDescription = "Date",
                        tint = lightBrown,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Date: ${event.date}",
                        color = lightBrown,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.location_pin),
                        contentDescription = "Distance",
                        tint = primaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.distance,
                        color = lightBrown,
                        fontSize = 14.sp
                    )
                }
            }

            Column(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalAlignment = Alignment.End
            ) {
                // Status indicator (Open or Closed)
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .border(
                            width = 1.dp,
                            color = if (event.status == EventStatus.OPEN) primaryGreen else Color(0xFF9E9E9E),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(
                            color = if (event.status == EventStatus.OPEN) Color(0xFFE0F2E9) else Color(0xFFEEEEEE),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (event.status == EventStatus.OPEN) "Open" else "Closed",
                        color = if (event.status == EventStatus.OPEN) primaryGreen else Color(0xFF757575),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                text = if (event.status == EventStatus.OPEN) "Open till 6 pm" else "Event ended",
                color = lightBrown,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}