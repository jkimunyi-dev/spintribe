package com.android.onboardingscreen.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.onboardingscreen.data.RegisteredEvent
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

private const val TAG = "EventQRCodeDialog"

@Composable
fun EventQRCodeDialog(
    event: RegisteredEvent,
    onDismiss: () -> Unit
) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(event) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Generating QR code for event: ${event.eventName}")
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix = multiFormatWriter.encode(
                    event.qrCodeData,
                    BarcodeFormat.QR_CODE,
                    400,  // Increased size for better readability
                    400
                )
                val barcodeEncoder = BarcodeEncoder()
                qrBitmap = barcodeEncoder.createBitmap(bitMatrix)
                Log.d(TAG, "QR code generated successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error generating QR code", e)
                errorMessage = "Failed to generate QR code: ${e.message}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Event QR Code") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                qrBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Event QR Code",
                        modifier = Modifier
                            .size(300.dp)
                            .padding(16.dp)
                    )
                } ?: CircularProgressIndicator()

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = event.eventName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Date: ${event.eventDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "Location: ${event.location}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                if (event.userEmail.isNotEmpty()) {
                    Text(
                        text = "Email: ${event.userEmail}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                if (event.phoneNumber.isNotEmpty()) {
                    Text(
                        text = "Phone: ${event.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
