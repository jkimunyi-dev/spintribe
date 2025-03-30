package com.android.onboardingscreen.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import kotlinx.coroutines.flow.first

private val Context.eventDataStore by preferencesDataStore(name = "registered_events")

private const val TAG = "RegisteredEventsManager"

@kotlinx.serialization.Serializable
data class RegisteredEvent(
    val eventId: String,
    val eventName: String,
    val registrationDate: String,
    val eventDate: String,
    val location: String,
    val distance: String = "",        // Made optional with default value
    val category: String = "",        // Made optional with default value
    val trackType: String = "",       // Made optional with default value
    val description: String = "",     // Made optional with default value
    val qrCodeData: String = "",      // New field for QR code data
    val userEmail: String = "",       // Added field
    val phoneNumber: String = ""      // Added field
)

@kotlinx.serialization.Serializable
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String = "EVENT_REGISTRATION"
)

class RegisteredEventsManager(private val context: Context) {
    private val registeredEventsKey = stringPreferencesKey("registered_events")
    private val notificationsKey = stringPreferencesKey("notifications")

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun registerForEvent(event: FeaturedEventData, phoneNumber: String) {
        try {
            Log.d(TAG, "Starting event registration for: ${event.name}")
            
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            
            // Get current user email
            val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
            
            // Generate QR code data
            val qrCodeData = buildString {
                append("Event: ${event.name}\n")
                append("Date: ${event.date}\n")
                append("Location: ${event.startLocation}\n")
                append("Category: ${event.category}\n")
                append("Distance: ${event.distance}\n")
                append("Registration Time: ${currentDateTime.format(formatter)}\n")
                append("Participant Email: $userEmail\n")
                append("Phone Number: $phoneNumber\n")
                append("ID: ${event.name.hashCode()}")
            }
            
            val registeredEvent = RegisteredEvent(
                eventId = event.name.hashCode().toString(),
                eventName = event.name,
                registrationDate = currentDateTime.format(formatter),
                eventDate = event.date,
                location = event.startLocation,
                distance = event.distance,
                category = event.category,
                trackType = event.trackType,
                description = "Join us for an unforgettable experience at ${event.name}. " +
                         "This event promises to bring together enthusiasts and professionals " +
                         "for an amazing day of activities and networking.",
                qrCodeData = qrCodeData,
                userEmail = userEmail,
                phoneNumber = phoneNumber
            )

            Log.d(TAG, "Created RegisteredEvent object: $registeredEvent")

            try {
                context.eventDataStore.edit { preferences ->
                    val existingEventsJson = preferences[registeredEventsKey] ?: "[]"
                    Log.d(TAG, "Existing events JSON: $existingEventsJson")
                    
                    val existingEvents = try {
                        Json.decodeFromString<List<RegisteredEvent>>(existingEventsJson)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to decode existing events, starting fresh", e)
                        emptyList()
                    }
                    
                    Log.d(TAG, "Successfully decoded existing events, count: ${existingEvents.size}")
                    
                    // Check if the event is already registered to avoid duplicates
                    val eventAlreadyExists = existingEvents.any { it.eventId == registeredEvent.eventId }
                    
                    val updatedEvents = if (eventAlreadyExists) {
                        Log.d(TAG, "Event already registered, not adding duplicate")
                        existingEvents
                    } else {
                        Log.d(TAG, "Adding new event to the list")
                        existingEvents + registeredEvent
                    }
                    
                    val updatedJson = Json.encodeToString(updatedEvents)
                    Log.d(TAG, "Encoded updated events JSON: $updatedJson")
                    
                    preferences[registeredEventsKey] = updatedJson
                    Log.d(TAG, "Successfully updated events in DataStore")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating registered events", e)
                throw e
            }

            // Create notification
            try {
                val notification = Notification(
                    id = currentDateTime.toString().hashCode().toString(),
                    title = "You're going to ${event.name}! 🎉",
                    message = "Mark your calendar for ${event.date} at ${event.startLocation}. " +
                             "Distance: ${event.distance}. We can't wait to see you there!",
                    timestamp = currentDateTime.format(formatter)
                )

                context.eventDataStore.edit { preferences ->
                    val existingNotificationsJson = preferences[notificationsKey] ?: "[]"
                    val existingNotifications = Json.decodeFromString<List<Notification>>(existingNotificationsJson)
                    val updatedNotifications = existingNotifications + notification
                    preferences[notificationsKey] = Json.encodeToString(updatedNotifications)
                }
                Log.d(TAG, "Successfully created notification")
            } catch (e: Exception) {
                Log.e(TAG, "Error creating notification", e)
                throw e
            }

            Log.d(TAG, "Event registration completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in registerForEvent", e)
            throw e
        }
    }

    suspend fun getRegisteredEvents(): List<RegisteredEvent> {
        return try {
            val eventsJson = context.eventDataStore.data.first()[registeredEventsKey] ?: "[]"
            Json.decodeFromString(eventsJson)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching registered events", e)
            emptyList()
        }
    }

    suspend fun getEventById(eventId: String): RegisteredEvent? {
        return try {
            val events = getRegisteredEvents()
            events.find { it.eventId == eventId }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching event by ID", e)
            null
        }
    }

    fun getNotifications(): Flow<List<Notification>> {
        return context.eventDataStore.data.map { preferences ->
            val notificationsJson = preferences[notificationsKey] ?: "[]"
            Json.decodeFromString(notificationsJson)
        }
    }
}
