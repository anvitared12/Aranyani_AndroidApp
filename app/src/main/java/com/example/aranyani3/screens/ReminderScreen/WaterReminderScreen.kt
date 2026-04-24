package com.example.aranyani3.screens.ReminderScreen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aranyani3.notification.schedulePlantReminder
import com.example.aranyani3.notification.scheduleTestNotification
import java.text.SimpleDateFormat
import java.util.*
import com.example.aranyani3.notification.schedulePlantReminder
import com.example.aranyani3.notification.scheduleTestNotification
private val HeaderGreen    = Color(0xFF1A4032)
private val AccentGreen    = Color(0xFF5EC49A)
private val PageBg         = Color(0xFFF0EDE6)
private val CardBg         = Color(0xFFFFFFFF)
private val FieldBorder    = Color(0xFFCBD5C0)
private val FieldBorderErr = Color(0xFFD32F2F)
private val ErrorRed       = Color(0xFFD32F2F)
private val LabelGray      = Color(0xFF6E7A6E)
private val HintGray       = Color(0xFFADB5A0)
private val IconGreen      = Color(0xFF2D7A52)
private val BtnGreen       = Color(0xFF1A4032)
private val SectionBg      = Color(0xFFEDF7F0)

@Composable
fun PlantReminderScreen(
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {}
) {
    val context = LocalContext.current

    var interval      by remember { mutableStateOf("") }
    var intervalError by remember { mutableStateOf<String?>(null) }

    var startDateMillis by remember { mutableStateOf<Long?>(null) }
    var startDateLabel  by remember { mutableStateOf("") }
    var dateError       by remember { mutableStateOf<String?>(null) }

    var selectedHour   by remember { mutableIntStateOf(9) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var timeLabel      by remember { mutableStateOf("09:00 AM") }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    val openDatePicker = {
        val calendar = Calendar.getInstance()
        startDateMillis?.let { calendar.timeInMillis = it }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val picked = Calendar.getInstance().apply {
                    set(year, month, day, selectedHour, selectedMinute, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                startDateMillis = picked.timeInMillis
                startDateLabel  = dateFormatter.format(picked.time)
                dateError       = null
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
        }.show()
    }

    val openTimePicker = {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                selectedHour   = hour
                selectedMinute = minute
                val cal = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                timeLabel = timeFormatter.format(cal.time)
                startDateMillis?.let { existing ->
                    val updated = Calendar.getInstance().apply {
                        timeInMillis = existing
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    startDateMillis = updated.timeInMillis
                }
            },
            selectedHour,
            selectedMinute,
            false
        ).show()
    }

    fun validate(): Boolean {
        var valid = true
        if (startDateMillis == null) {
            dateError = "Please select a start date"
            valid = false
        } else {
            val combined = Calendar.getInstance().apply {
                timeInMillis = startDateMillis!!
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (combined.timeInMillis <= System.currentTimeMillis()) {
                dateError = "Start date & time must be in the future"
                valid = false
            } else {
                dateError = null
            }
        }
        val days = interval.toLongOrNull()
        when {
            interval.isBlank()        -> { intervalError = "Please enter repeat interval"; valid = false }
            days == null || days <= 0 -> { intervalError = "Interval must be at least 1 day"; valid = false }
            days > 365                -> { intervalError = "Interval cannot exceed 365 days"; valid = false }
            else                      -> intervalError = null
        }
        return valid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeaderGreen)
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onBack() }
                )
                Column {
                    Text("Plant Reminder", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Schedule watering notifications", fontSize = 12.sp, color = AccentGreen)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SectionBg)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🌱", fontSize = 48.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Never forget to water your plants",
                        fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        color = IconGreen, textAlign = TextAlign.Center
                    )
                    Text(
                        "Set a date, time, and repeat interval",
                        fontSize = 12.sp, color = LabelGray, textAlign = TextAlign.Center
                    )
                }
            }

            // Date
            SectionLabel("Start Date")
            PickerField(
                label = if (startDateLabel.isEmpty()) "Select start date" else startDateLabel,
                isPlaceholder = startDateLabel.isEmpty(),
                hasError = dateError != null,
                trailingIcon = {
                    Icon(Icons.Default.DateRange, null, tint = IconGreen, modifier = Modifier.size(22.dp))
                },
                onClick = { openDatePicker() }
            )
            ErrorText(dateError)

            // Time
            SectionLabel("Reminder Time")
            PickerField(
                label = timeLabel,
                isPlaceholder = false,
                hasError = false,
                trailingIcon = { Text("🕐", fontSize = 20.sp) },
                onClick = { openTimePicker() }
            )

            // Interval
            SectionLabel("Repeat Every (days)")
            OutlinedTextField(
                value = interval,
                onValueChange = {
                    interval = it.filter { ch -> ch.isDigit() }.take(3)
                    intervalError = null
                },
                placeholder = { Text("e.g. 3", color = HintGray, fontSize = 14.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = intervalError != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IconGreen,
                    unfocusedBorderColor = FieldBorder,
                    errorBorderColor = FieldBorderErr,
                    focusedLabelColor = IconGreen,
                    cursorColor = IconGreen
                ),
                supportingText = {
                    AnimatedVisibility(visible = intervalError != null) {
                        Text(intervalError ?: "", color = ErrorRed, fontSize = 12.sp)
                    }
                }
            )

            // Summary card
            val days = interval.toLongOrNull()
            AnimatedVisibility(
                visible = startDateLabel.isNotEmpty() && days != null && days > 0,
                enter = fadeIn(), exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SectionBg)
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("📋 Reminder Summary", fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold, color = IconGreen)
                        SummaryRow("First reminder", "$startDateLabel at $timeLabel")
                        SummaryRow("Repeats every", "${days ?: "—"} day(s)")
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── TEST button — fires a notification in 10 seconds ──
            OutlinedButton(
                onClick = {
                    scheduleTestNotification(context)
                    Toast.makeText(
                        context,
                        "Test notification coming in ~10 seconds!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(IconGreen)
                )
            ) {
                Text("Test Notification (10s)", fontSize = 14.sp, color = IconGreen)
            }

            // ── Main save button ──
            Button(
                onClick = {
                    if (validate()) {
                        val finalMillis = Calendar.getInstance().apply {
                            timeInMillis = startDateMillis!!
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.timeInMillis

                        schedulePlantReminder(context, finalMillis, interval.toLong())

                        Toast.makeText(
                            context,
                            "Reminder set for $startDateLabel at $timeLabel, every $interval day(s) 🌱",
                            Toast.LENGTH_LONG
                        ).show()
                        onSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BtnGreen)
            ) {
                Text("Set Reminder 🌱", fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
        color = LabelGray, modifier = Modifier.padding(bottom = 2.dp))
}

@Composable
private fun PickerField(
    label: String,
    isPlaceholder: Boolean,
    hasError: Boolean,
    trailingIcon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .border(1.dp, if (hasError) FieldBorderErr else FieldBorder, RoundedCornerShape(12.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = if (isPlaceholder) HintGray else Color(0xFF1A2E1F),
                modifier = Modifier.weight(1f)
            )
            trailingIcon()
        }
    }
}

@Composable
private fun ErrorText(message: String?) {
    AnimatedVisibility(visible = message != null) {
        Text(message ?: "", fontSize = 12.sp, color = ErrorRed,
            modifier = Modifier.padding(start = 4.dp, top = 2.dp))
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = LabelGray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A2E1F))
    }
}