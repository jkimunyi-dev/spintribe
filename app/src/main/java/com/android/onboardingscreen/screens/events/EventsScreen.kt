package com.android.onboardingscreen.screens.events

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.onboardingscreen.data.RegisteredEventsManager
import com.android.onboardingscreen.data.RegisteredEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.android.onboardingscreen.screens.home.RegisteredEventsList

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Events",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        RegisteredEventsList(
            events = registeredEvents,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}
