package com.android.onboardingscreen.components

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.onboardingscreen.R
import com.android.onboardingscreen.data.FeaturedEventData
import com.android.onboardingscreen.data.RegisteredEventsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

private const val TAG = "EventDetailSheet"

private val mpesaPhoneRegex = Regex("^0[0-9]{9}$") // Matches 0 followed by 9 digits

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EventDetailSheet(
    event: FeaturedEventData,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> EventDetailsPage(
                        event = event,
                        isPaidEvent = event.isPaid,
                        onNextClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                    1 -> RegistrationPage(
                        event = event,
                        fullName = fullName,
                        onFullNameChange = { fullName = it },
                        isPaidEvent = event.isPaid,
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
            text = event.description.ifEmpty { 
                "Join us for an unforgettable experience at ${event.name}. " +
                "This event promises to bring together enthusiasts and professionals " +
                "for an amazing day of activities and networking."
            },
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

        val daysRemaining = try {
            val eventDate = LocalDate.parse(event.date, formatter)
            ChronoUnit.DAYS.between(LocalDate.now(), eventDate)
        } catch (e: Exception) {
            android.util.Log.e("EventDetailsPage", "Error parsing date: ${event.date}", e)
            0L
        }
        
        Text(
            text = "$daysRemaining days remaining",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "${event.totalAttendees} people going",
            fontSize = 14.sp
        )

        if (event.attendees.isNotEmpty()) {
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
                    event.attendees.take(2).forEachIndexed { index, attendee ->
                        Image(
                            painter = painterResource(id = R.drawable.person1), // Replace with actual image loading
                            contentDescription = attendee.name,
                            modifier = Modifier
                                .size(24.dp)
                                .offset(x = (16 * index).dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                Text(
                    text = event.attendees.take(2).joinToString(", ") { it.name },
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            color = if (isPaidEvent) Color(0xFFFFE0B2) else Color(0xFFE8F5E9),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = if (isPaidEvent) "Paid Event (${event.price} KES)" else "Free Event",
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
    event: FeaturedEventData,
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
    
    // Add state for phone number
    var phoneNumber by remember { mutableStateOf("") }
    // Add state for validation
    var isFormValid by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf<String?>(null) }
    
    // Validate M-Pesa number
    fun isValidMpesaNumber(number: String): Boolean {
        return mpesaPhoneRegex.matches(number)
    }

    // Process M-Pesa payment
    suspend fun processMpesaPayment(phoneNumber: String) {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val json = JSONObject().apply {
                    put("phone", phoneNumber)
                    put("accountNumber", "TEST001") // Updated account number
                    put("amount", "1")
                }

                val request = Request.Builder()
                    .url("https://daraja-node.vercel.app/api/stkpush")
                    .post(
                        json.toString()
                            .toRequestBody("application/json".toMediaType())
                    )
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    when (response.code) {
                        200 -> {
                            paymentError = null
                            Log.d(TAG, "M-Pesa payment initiated successfully")
                        }
                        400 -> {
                            val errorMessage = try {
                                JSONObject(responseBody ?: "").optString("message", "Bad Request")
                            } catch (e: Exception) {
                                "Bad Request"
                            }
                            throw IOException("Payment failed: $errorMessage")
                        }
                        else -> {
                            throw IOException("Payment failed: ${response.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing M-Pesa payment", e)
                paymentError = e.message ?: "Payment failed"
                throw e
            }
        }
    }

    // Validation function
    fun validateForm(): Boolean {
        return fullName.isNotBlank() &&
               phoneNumber.isNotBlank() && 
               phoneNumber.length >= 10 &&
               (!isPaidEvent || (selectedPaymentMethod != null && paymentDetails.isNotBlank()))
    }

    // Update validation when form changes
    LaunchedEffect(fullName, phoneNumber, selectedPaymentMethod, paymentDetails) {
        isFormValid = validateForm()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && fullName.isBlank(),
            supportingText = if (showError && fullName.isBlank()) {
                { Text("Name is required") }
            } else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add phone number field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { 
                if (it.length <= 12) { // Limit phone number length
                    phoneNumber = it.filter { char -> char.isDigit() } // Only allow digits
                }
            },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            isError = showError && (phoneNumber.isBlank() || phoneNumber.length < 10),
            supportingText = if (showError && phoneNumber.isBlank()) {
                { Text("Phone number is required") }
            } else if (showError && phoneNumber.length < 10) {
                { Text("Enter a valid phone number") }
            } else null
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

            if (showError && selectedPaymentMethod == null) {
                Text(
                    text = "Please select a payment method",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Payment details input field
            selectedPaymentMethod?.let { method ->
                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    OutlinedTextField(
                        value = paymentDetails,
                        onValueChange = { input ->
                            when (method) {
                                "M-PESA" -> {
                                    if (input.length <= 10) {
                                        onPaymentDetailsChange(input.filter { it.isDigit() })
                                        paymentError = null
                                    }
                                }
                                else -> onPaymentDetailsChange(input)
                            }
                        },
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
                        isError = when (method) {
                            "M-PESA" -> showError && (!paymentDetails.isBlank() && !isValidMpesaNumber(paymentDetails)) || paymentError != null
                            else -> showError && paymentDetails.isBlank()
                        },
                        supportingText = when {
                            method == "M-PESA" && showError && !paymentDetails.isBlank() && !isValidMpesaNumber(paymentDetails) -> {
                                { Text("Enter valid M-Pesa number (e.g., 0712345678)") }
                            }
                            showError && paymentDetails.isBlank() -> {
                                { Text("Payment details are required") }
                            }
                            else -> null
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = when (method) {
                                "M-PESA" -> KeyboardType.Phone
                                "KCB", "NCBA" -> KeyboardType.Number
                                else -> KeyboardType.Text
                            }
                        )
                    )

                    // Display payment error if any
                    if (method == "M-PESA") {
                        paymentError?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                showError = true
                if (isFormValid) {
                    scope.launch {
                        try {
                            isProcessingPayment = true
                            paymentError = null // Clear any previous errors
                            
                            // Process M-Pesa payment if selected
                            if (selectedPaymentMethod == "M-PESA") {
                                processMpesaPayment(paymentDetails)
                            }
                            
                            // Continue with registration
                            eventsManager.registerForEvent(event, phoneNumber)
                            
                            Toast.makeText(
                                context,
                                "Successfully registered for ${event.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                            
                            onNextClick()
                        } catch (e: Exception) {
                            Log.e(TAG, "Registration/Payment failed", e)
                            paymentError = e.message ?: "Payment failed"
                            Toast.makeText(
                                context,
                                "Failed: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            isProcessingPayment = false
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = isFormValid && !isProcessingPayment
        ) {
            if (isProcessingPayment) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Continue")
            }
        }
    }
}

@Composable
private fun SuccessPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.trophy),
            contentDescription = "Success",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Registration Successful!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "You have successfully registered for the event.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

data class PaymentMethod(
    val name: String,
    @DrawableRes val icon: Int
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
