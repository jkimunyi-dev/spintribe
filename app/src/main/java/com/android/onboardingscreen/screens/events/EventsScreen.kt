package com.android.onboardingscreen.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.onboardingscreen.data.RegisteredEventsManager
import com.android.onboardingscreen.data.RegisteredEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun EventsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val eventsManager = remember { RegisteredEventsManager(context) }
    var registeredEvents by remember { mutableStateOf(emptyList<RegisteredEvent>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            registeredEvents = eventsManager.getRegisteredEvents()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Events",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (registeredEvents.isEmpty()) {
            EmptyEventsView()
        } else {
            RegisteredEventsList(events = registeredEvents)
        }
    }
}

@Composable
private fun EmptyEventsView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "You haven't registered for any events yet",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RegisteredEventsList(events: List<RegisteredEvent>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events) { event ->
            EventCard(event = event)
        }
    }
}

@Composable
private fun EventCard(event: RegisteredEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.eventName,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Date: ${event.eventDate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Location: ${event.location}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (event.distance.isNotEmpty()) {
                Text(
                    text = "Distance: ${event.distance}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
