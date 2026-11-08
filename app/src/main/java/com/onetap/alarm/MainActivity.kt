package com.onetap.alarm

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OneTapAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F172A)
                ) {
                    OneTapAlarmScreen(
                        onSetAlarm = { hour, minute ->
                            setAlarm(hour, minute)
                        }
                    )
                }
            }
        }
    }

    private fun setAlarm(hour: Int, minute: Int) {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val formattedTime = String.format("%d:%02d %s", displayHour, minute, amPm)

        try {
            // Samsung & Standard Android AlarmClock intent
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, "One Tap Alarm")
                // Skip UI confirmation if supported by device clock (Samsung Clock supports this)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
                Toast.makeText(this, "Alarm set for $formattedTime", Toast.LENGTH_SHORT).show()
            } else {
                // Fallback: Open clock app directly
                val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                if (clockIntent.resolveActivity(packageManager) != null) {
                    startActivity(clockIntent)
                } else {
                    Toast.makeText(this, "No Clock app found on this device", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Could not set alarm: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}

data class PresetTime(val label: String, val hour: Int, val minute: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneTapAlarmScreen(onSetAlarm: (hour: Int, minute: Int) -> Unit) {
    val presets = remember {
        listOf(
            PresetTime("5:00 AM", 5, 0),
            PresetTime("5:30 AM", 5, 30),
            PresetTime("6:00 AM", 6, 0),
            PresetTime("6:30 AM", 6, 30),
            PresetTime("7:00 AM", 7, 0),
            PresetTime("7:30 AM", 7, 30),
            PresetTime("8:00 AM", 8, 0),
            PresetTime("8:30 AM", 8, 30),
            PresetTime("9:00 AM", 9, 0),
            PresetTime("9:30 AM", 9, 30),
        )
    }

    var lastSetTime by remember { mutableStateOf<String?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }

    val currentTime = remember {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        Pair(hour, minute)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.first,
        initialMinute = currentTime.second,
        is24Hour = false
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Title Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = null,
                tint = Color(0xFF818CF8),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "One Tap Alarm",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Text(
            text = "Tap any preset to set your Samsung Clock alarm instantly",
            fontSize = 13.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
        )

        // Status Card
        if (lastSetTime != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Last requested: $lastSetTime",
                        color = Color(0xFFA7F3D0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Presets Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(presets) { preset ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            lastSetTime = preset.label
                            onSetAlarm(preset.hour, preset.minute)
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = preset.label,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2E8F0)
                            )
                            Text(
                                text = "One Tap",
                                fontSize = 11.sp,
                                color = Color(0xFF818CF8),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom Time Picker Button
        Button(
            onClick = { showTimePicker = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Pick Custom Time", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val h = timePickerState.hour
                        val m = timePickerState.minute
                        val amPm = if (h < 12) "AM" else "PM"
                        val displayHour = when {
                            h == 0 -> 12
                            h > 12 -> h - 12
                            else -> h
                        }
                        val formatted = String.format("%d:%02d %s", displayHour, m, amPm)
                        lastSetTime = formatted
                        onSetAlarm(h, m)
                        showTimePicker = false
                    }
                ) {
                    Text("Set Alarm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@Composable
fun OneTapAlarmTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF6366F1),
            secondary = Color(0xFF10B981),
            background = Color(0xFF0F172A),
            surface = Color(0xFF1E293B)
        ),
        content = content
    )
}
