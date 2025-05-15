package com.example.sawaapplication.screens.event.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


fun formatTimestampToTimeString(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(date)
}

@Composable
fun TimePickerModal(
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        ShowKeyboardTimePicker(
            onTimeSelected = { hour, minute ->
                onTimeSelected(hour, minute)
                showDialog = false
            },
            onDismiss = {
                showDialog = false
                onDismiss()
            }
        )
    }
}


@Composable
fun ShowKeyboardTimePicker(
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(1) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var ampm by remember { mutableStateOf("AM") }


    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Select Time",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    NumberPickerView(value = selectedHour, range = 1..12) { value -> selectedHour = value }
                    Text(text = ":", modifier = Modifier.align(Alignment.CenterVertically))
                    NumberPickerView(value = selectedMinute, range = 0..59) { value -> selectedMinute = value }
                    AmPmPicker(value = ampm) { newValue -> ampm = newValue }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val adjustedHour = if (ampm == "AM" && selectedHour == 12) 0 else if (ampm == "PM" && selectedHour != 12) selectedHour + 12 else selectedHour
                    onTimeSelected(adjustedHour, selectedMinute)
                    onDismiss()
                }) {
                    Text(text = "Set Time")
                }

            }
        }
    }
}

@Composable
fun NumberPickerView(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    AndroidView(
        factory = { ctx ->
            val numberPicker = android.widget.NumberPicker(ctx)
            numberPicker.minValue = range.first
            numberPicker.maxValue = range.last
            numberPicker.value = value
            numberPicker.setOnValueChangedListener { _, _, newVal ->
                onValueChange(newVal)
            }
            numberPicker
        },
        update = { numberPicker ->
            numberPicker.value = value
        },
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
fun AmPmPicker(
    value: String,
    onValueChange: (String) -> Unit
) {
    val options = listOf("AM", "PM")

    AndroidView(
        factory = { ctx ->
            val numberPicker = android.widget.NumberPicker(ctx)
            numberPicker.minValue = 0
            numberPicker.maxValue = 1
            numberPicker.value = if (value == "PM") 1 else 0
            numberPicker.displayedValues = options.toTypedArray()
            numberPicker.setOnValueChangedListener { _, _, newVal ->
                onValueChange(options[newVal])
            }
            numberPicker
        },
        update = { numberPicker ->
            numberPicker.value = if (value == "PM") 1 else 0
        },
        modifier = Modifier.padding(8.dp)
    )
}
