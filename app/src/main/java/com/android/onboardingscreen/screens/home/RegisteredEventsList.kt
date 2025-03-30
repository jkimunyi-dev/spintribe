package com.android.onboardingscreen.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.onboardingscreen.components.EventQRCodeDialog
import com.android.onboardingscreen.data.RegisteredEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.delay

@Composable
fun RegisteredEventItem(
    event: RegisteredEvent,
    modifier: Modifier = Modifier,
    onShowQRCode: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .width(360.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp) // Make space for QR code
            ) {
                Text(
                    text = event.eventName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.eventDate,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    EventCountdownIndicator(event.eventDate)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "Registered on: ${event.registrationDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // QR Code button in bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    IconButton(
                        onClick = onShowQRCode,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "Show QR Code",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCountdownIndicator(eventDate: String) {
    var daysRemaining by remember { mutableStateOf(0L) }
    
    LaunchedEffect(eventDate) {
        while (true) {
            val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
            val targetDate = LocalDate.parse(eventDate, formatter).atStartOfDay()
            val currentDate = LocalDateTime.now()
            daysRemaining = ChronoUnit.DAYS.between(currentDate, targetDate)
            delay(3600000) // Update every hour
        }
    }

    if (daysRemaining in 0..2) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$daysRemaining days left",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
private fun LabeledText(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RegisteredEventsList(
    events: List<RegisteredEvent>,
    modifier: Modifier = Modifier
) {
    var selectedEvent by remember { mutableStateOf<RegisteredEvent?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (events.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No registered events yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(events) { event ->
                RegisteredEventItem(
                    event = event,
                    onShowQRCode = { selectedEvent = event }
                )
            }
        }
    }

    selectedEvent?.let { event ->
        EventQRCodeDialog(
            event = event,
            onDismiss = { selectedEvent = null }
        )
    }
}
