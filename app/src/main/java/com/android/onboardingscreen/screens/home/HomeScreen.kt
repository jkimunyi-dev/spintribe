// screens/home/HomeScreen.kt
package com.android.onboardingscreen.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import com.android.onboardingscreen.components.EventDetailSheet
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import com.android.onboardingscreen.data.EventData
import com.android.onboardingscreen.data.EventStatus
import com.android.onboardingscreen.data.FeaturedEventData
import com.android.onboardingscreen.utils.JsonLoader

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val instrumentSans = FontFamily(
        Font(R.font.instrument_sans_semibold, FontWeight.SemiBold)
    )
    var showEventDetail by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<FeaturedEventData?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val tabs = listOf("All", "Upcoming", "Past")

    // Create functions to handle both types of events
    fun showFeaturedEventDetail(event: FeaturedEventData) {
        selectedEvent = event
        showEventDetail = true
    }

    fun showEventDetail(event: EventData) {
        selectedEvent = FeaturedEventData(
            name = event.name,
            date = event.date,
            startLocation = "Location", // Default location
            trackType = "Standard",     // Default track type
            averageSpeed = "N/A",
            distance = event.distance,
            backgroundColorString = "#1E7A56", // Add this parameter
            category = event.category,
            endTime = event.endTime,
            description = "", // Add default empty description
            price = 0,       // Add default price
            isPaid = false,  // Add default isPaid
            attendees = emptyList(), // Add empty attendees list
            totalAttendees = 0      // Add default total attendees
        )
        showEventDetail = true
    }

    // Adventure theme colors
    val primaryGreen = Color(0xFF1E7A56)
    val darkBrown = Color(0xFF3E2723)
    val lightBrown = Color(0xFF8D6E63)
    val skyBlue = Color(0xFF64B5F6)
    val sunsetOrange = Color(0xFFFF7043)

    val context = LocalContext.current
    val eventsData = remember { JsonLoader.loadEventsData(context) }
    
    val featuredEvents = remember { eventsData.featuredEvents }
    
    val allEvents = remember { mutableStateListOf<EventData>().apply {
        addAll(eventsData.regularEvents)
    }}

    // Filter events based on search query, selected tab, and category
    val filteredEvents = remember(searchQuery, selectedTab, selectedCategory) {
        allEvents.filter { event ->
            val matchesSearch = event.name.contains(searchQuery, ignoreCase = true)
            val matchesTab = when (selectedTab) {
                0 -> true // All events
                1 -> event.status == EventStatus.OPEN // Upcoming
                2 -> event.status == EventStatus.CLOSED // Past
                else -> true
            }
            val matchesCategory = selectedCategory?.let { event.category == it } ?: true
            matchesSearch && matchesTab && matchesCategory
        }
    }

    // Filter featured events based on selected category
    val filteredFeaturedEvents = remember(selectedCategory) {
        when (selectedCategory) {
            null -> featuredEvents // Show all featured events when no category is selected
            else -> featuredEvents.filter { it.category == selectedCategory }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp) // Remove default spacing
        ) {
            // Top Bar
            item {
                TopAppBar(
                    title = {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        Text(
                            text = "Hello ${currentUser?.email?.substringBefore('@') ?: "there"}",
                            fontFamily = instrumentSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = darkBrown,
                            modifier = Modifier.padding(top = 12.dp)  // Added padding top
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

            // Categories section
            item {
                val categories = listOf(
                    "All",  // Added "All" as the first category
                    "Academic", "Sports", "Cultural", "Club", "Career",
                    "Volunteer", "Tech", "Music", "Culinary"
                )
                
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp) // Reduced padding
                ) {
                    items(categories) { category ->
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = if (selectedCategory == category) primaryGreen else primaryGreen.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(30)
                                )
                                .clip(RoundedCornerShape(10))
                                .clickable { 
                                    selectedCategory = when (category) {
                                        "All" -> null  // Set to null when "All" is selected
                                        else -> if (selectedCategory == category) null else category
                                    }
                                }
                                .background(
                                    if (selectedCategory == category || (category == "All" && selectedCategory == null)) 
                                        primaryGreen.copy(alpha = 0.1f) 
                                    else 
                                        Color.Transparent
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category,
                                color = if (selectedCategory == category || (category == "All" && selectedCategory == null)) 
                                    primaryGreen 
                                else 
                                    darkBrown,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            // Search bar
            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp), // Reduced padding
                    placeholder = { 
                        Text(
                            "Search Adventure...", 
                            color = lightBrown,
                            fontWeight = FontWeight.SemiBold
                        ) 
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = primaryGreen
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // Featured event carousel
            item {
                if (filteredFeaturedEvents.isNotEmpty()) {
                    Text(
                        text = "Featured",
                        fontFamily = instrumentSans,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = darkBrown,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp) // Reduced padding
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredFeaturedEvents) { event ->
                            FeaturedEventCard(
                                event = event,
                                onClick = { showFeaturedEventDetail(event) }
                            )
                        }
                    }
                }
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
                EventListItem(
                    event = event,
                    primaryGreen = primaryGreen,
                    darkBrown = darkBrown,
                    lightBrown = lightBrown,
                    onClick = { showEventDetail(event) }
                )
            }

            // Add empty space at the bottom
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showEventDetail && selectedEvent != null) {
        EventDetailSheet(
            event = selectedEvent!!,
            onDismiss = { showEventDetail = false }
        )
    }
}

data class EventData(
    val name: String,
    val date: String,
    val distance: String,
    val status: EventStatus,
    val category: String,
    val endTime: String // Add end time
)

enum class EventStatus {
    OPEN,
    CLOSED
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventTimer(endTime: String) {
    var timeRemaining by remember { mutableStateOf("") }
    
    LaunchedEffect(endTime) {
        while(true) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val targetTime = LocalTime.parse(endTime, formatter)
            val currentTime = LocalTime.now()
            
            val duration = Duration.between(currentTime, targetTime)
            timeRemaining = if (duration.isNegative) {
                "Event ended"
            } else {
                String.format("%02d:%02d", duration.toHours(), duration.toMinutesPart())
            }
            
            delay(1000) // Update every second
        }
    }
    
    Text(
        text = timeRemaining,
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun FeaturedEventCard(
    event: FeaturedEventData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(190.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ),
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
            Column {
                Text(
                    text = event.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = event.date,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Text(
                    text = event.startLocation,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier
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

            // Category chip
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = event.category,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EventListItem(
    event: EventData,
    primaryGreen: Color,
    darkBrown: Color,
    lightBrown: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ),
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
                modifier = Modifier.align(Alignment.BottomEnd),
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

                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (event.status == EventStatus.OPEN) "Open till 6 pm" else "Event ended",
                    color = lightBrown,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}

