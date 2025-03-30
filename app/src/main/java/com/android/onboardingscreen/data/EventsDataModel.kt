package com.android.onboardingscreen.data

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class EventsData(
    val featuredEvents: List<FeaturedEventData>,
    val regularEvents: List<EventData>,
    val categories: List<String>,
    val paymentMethods: List<PaymentMethod>
)

@Serializable
data class Attendee(
    val name: String,
    val photoUrl: String
)

@Serializable
data class FeaturedEventData(
    val name: String,
    val date: String,
    val startLocation: String,
    val trackType: String,
    val averageSpeed: String,
    val distance: String,
    @SerializedName("backgroundColor")
    private val backgroundColorString: String,
    val category: String,
    val endTime: String,
    val description: String = "",
    val price: Int = 0,
    val isPaid: Boolean = false,
    val attendees: List<Attendee> = emptyList(),
    val totalAttendees: Int = 0
) {
    val backgroundColor: Color
        get() = Color(android.graphics.Color.parseColor(backgroundColorString))
}

@Serializable
data class EventData(
    val name: String,
    val date: String,
    val distance: String,
    val status: EventStatus,
    val category: String,
    val endTime: String
)

enum class EventStatus {
    OPEN,
    CLOSED
}

@Serializable
data class PaymentMethod(
    val name: String,
    val icon: String
)
