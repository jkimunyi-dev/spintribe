package com.android.onboardingscreen.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormattedDate(): String {
    val date = Date(this)
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDatePicker(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    if (showDialog) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(it.toFormattedDate())
                    }
                    onDismiss()
                }) {
                    Text("OK", color = Color(0xFF173753))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = Color(0xFF173753))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF173753),
                    headlineContentColor = Color(0xFF173753),
                    weekdayContentColor = Color(0xFF173753),
                    subheadContentColor = Color(0xFF173753),
                    yearContentColor = Color(0xFF173753),
                    currentYearContentColor = Color(0xFF173753),
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = Color(0xFF173753),
                    dayContentColor = Color(0xFF173753),
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Color(0xFF173753),
                    todayContentColor = Color(0xFF173753),
                    todayDateBorderColor = Color(0xFF173753)
                )
            )
        }
    }
}