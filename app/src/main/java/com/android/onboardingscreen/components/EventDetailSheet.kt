package com.android.onboardingscreen.components

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.onboardingscreen.R
import com.android.onboardingscreen.screens.home.FeaturedEventData
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.platform.LocalContext
import com.android.onboardingscreen.data.RegisteredEventsManager
import java.util.Locale
import android.util.Log

private const val TAG = "EventDetailSheet"

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EventDetailSheet(
    event: FeaturedEventData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val eventsManager = remember { RegisteredEventsManager(context) }
    val pagerState = rememberPagerState(pageCount = { 3 })
    var fullName by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf<String?>(null) }
    var paymentDetails by remember { mutableStateOf("") }

    val paymentMethods = listOf(
        PaymentMethod("M-PESA", R.drawable.mpesa_logo),
        PaymentMethod("KCB", R.drawable.kcb_logo),
        PaymentMethod("NCBA", R.drawable.ncba_logo),
        PaymentMethod("USDC", R.drawable.usdc_logo),
        PaymentMethod("USDT", R.drawable.usdt_logo),
        PaymentMethod("Bitcoin", R.drawable.bitcoin_logo)
    )

    val isPaidEvent = true

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // HorizontalPager takes most of the space but leaves room for indicators
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> EventDetailsPage(
                        event = event,
                        isPaidEvent = isPaidEvent,
                        onNextClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                    1 -> RegistrationPage(
                        event = event, // Add this parameter
                        fullName = fullName,
                        onFullNameChange = { fullName = it },
                        isPaidEvent = isPaidEvent,
                        selectedPaymentMethod = selectedPaymentMethod,
                        paymentMethods = paymentMethods,
                        onPaymentMethodSelected = { selectedPaymentMethod = it },
                        paymentDetails = paymentDetails,
                        onPaymentDetailsChange = { paymentDetails = it },
                        onNextClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        }
                    )
                    2 -> SuccessPage()
                }
            }

            // Page indicators always at the bottom
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { iteration ->
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == iteration) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    Color.Gray
                            )
                            .size(8.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EventDetailsPage(
    event: FeaturedEventData,
    isPaidEvent: Boolean,
    onNextClick: () -> Unit
) {
    // Update the formatter to match the actual date format
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = event.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Join us for an unforgettable experience at ${event.name}. " +
                   "This event promises to bring together enthusiasts and professionals " +
                   "for an amazing day of activities and networking.",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            EventDetailRow(
                icon = R.drawable.location_icon,
                label = "Location",
                value = event.startLocation
            )
            EventDetailRow(
                icon = R.drawable.location_pin,
                label = "Distance",
                value = event.distance
            )
        }

        // Wrap the date parsing in a try-catch block for safety
        val daysRemaining = try {
            val eventDate = LocalDate.parse(event.date, formatter)
            ChronoUnit.DAYS.between(LocalDate.now(), eventDate)
        } catch (e: Exception) {
            // Log the error and return a default value
            android.util.Log.e("EventDetailsPage", "Error parsing date: ${event.date}", e)
            0L
        }
        
        Text(
            text = "$daysRemaining days remaining",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "156 people going",
            fontSize = 14.sp
        )

        // Attending section
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Attending:",
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Box {
                Image(
                    painter = painterResource(id = R.drawable.person1),
                    contentDescription = "Jimmy Kimunyi",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Image(
                    painter = painterResource(id = R.drawable.person2),
                    contentDescription = "Dennis Peter",
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = 16.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Text(
                text = "Jimmy Kimunyi, Dennis Peter",
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 32.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Event type indicator
        Surface(
            color = if (isPaidEvent) Color(0xFFFFE0B2) else Color(0xFFE8F5E9),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = if (isPaidEvent) "Paid Event" else "Free Event",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (isPaidEvent) Color(0xFFE65100) else Color(0xFF2E7D32)
            )
        }

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Attend")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationPage(
    event: FeaturedEventData, // Add this parameter
    fullName: String,
    onFullNameChange: (String) -> Unit,
    isPaidEvent: Boolean,
    selectedPaymentMethod: String?,
    paymentMethods: List<PaymentMethod>,
    onPaymentMethodSelected: (String) -> Unit,
    paymentDetails: String,
    onPaymentDetailsChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val eventsManager = remember { RegisteredEventsManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        if (isPaidEvent) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select Payment Method",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(paymentMethods) { method ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedPaymentMethod == method.name)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                            .clickable { onPaymentMethodSelected(method.name) }
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = method.icon),
                            contentDescription = method.name,
                            modifier = Modifier.size(40.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = method.name,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            selectedPaymentMethod?.let { method ->
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = paymentDetails,
                    onValueChange = onPaymentDetailsChange,
                    label = { 
                        Text(when (method) {
                            "M-PESA" -> "M-PESA Phone Number"
                            "KCB", "NCBA" -> "Card Number"
                            "USDC", "USDT" -> "Wallet Address"
                            "Bitcoin" -> "Lightning Wallet Address"
                            else -> "Payment Details"
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (method) {
                            "M-PESA" -> KeyboardType.Phone
                            "KCB", "NCBA" -> KeyboardType.Number
                            else -> KeyboardType.Text
                        }
                    )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                Log.d(TAG, "Registration button clicked for event: ${event.name}")
                scope.launch {
                    try {
                        Log.d(TAG, "Starting registration process...")
                        eventsManager.registerForEvent(event)
                        Log.d(TAG, "Registration successful")
                        
                        Toast.makeText(
                            context,
                            "Successfully registered for ${event.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        onNextClick()
                    } catch (e: Exception) {
                        Log.e(TAG, "Registration failed", e)
                        val errorMessage = e.message ?: "Unknown error occurred"
                        Log.e(TAG, "Error message: $errorMessage")
                        Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
                        
                        Toast.makeText(
                            context,
                            "Failed to register: $errorMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Continue")
        }
    }
}

@Composable
private fun SuccessPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Registration Successful!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You're all set for the event!",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "A confirmation email has been sent to your registered email address with all the event details.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "See you there! 🎉",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

data class PaymentMethod(
    val name: String,
    val icon: Int
)

@Composable
private fun EventDetailRow(
    @DrawableRes icon: Int,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 16.sp
            )
        }
    }
}
