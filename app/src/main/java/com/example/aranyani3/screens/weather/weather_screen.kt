package com.example.aranyani3.screens.weather

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aranyani3.viewmodel.WeatherViewModel
import com.example.aranyani3.models.HourlyWeather
import kotlinx.coroutines.delay
import kotlin.collections.firstOrNull

// ─────────────────────────────────────────────
// Color Palette — Clean Light Theme
// ─────────────────────────────────────────────
val PageBg        = Color(0xFFFFFFFF)
val CardWhite     = Color(0xFFC0CA33)
val ChipBg        = Color(0xFFE8EFF7)
val PrimaryBlue   = Color(0xFF4A90D9)
val SoftBlue      = Color(0xFF6AABEC)
val TextDark      = Color(0xFF1A1F2E)
val TextMid       = Color(0xFF5A6478)
val TextLight     = Color(0xFF9AAABB)
val AlertRed      = Color(0xFFFF5C5C)
val AlertBg       = Color(0xFFFFF0F0)

// ─────────────────────────────────────────────
// Weather Screen
// ─────────────────────────────────────────────
@Composable
fun WeatherScreen(
    latitude: Double = 12.9716,
    longitude: Double = 77.5946,
    viewModel: WeatherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(latitude, longitude) {
        viewModel.fetchWeather(latitude, longitude)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorCard(uiState.error!!) {
                viewModel.fetchWeather(latitude, longitude)
            }
            else -> WeatherContent(uiState)
        }
    }
}

// ─────────────────────────────────────────────
// Weather Content
// ─────────────────────────────────────────────
@Composable
fun WeatherContent(state: com.example.aranyani3.viewmodel.WeatherUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Hero Card
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { -30 })
            ) {
                CurrentWeatherHero(state)
            }
        }

        // Detailed hourly list
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Today's Details",
                color = TextMid,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        itemsIndexed(state.hourlyData) { index, hour ->
            AnimatedWeatherCard(hour = hour, index = index)
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ─────────────────────────────────────────────
// Hero Card (Current)
// ─────────────────────────────────────────────
@Composable
fun CurrentWeatherHero(state: com.example.aranyani3.viewmodel.WeatherUiState) {
    val current = state.hourlyData.firstOrNull() ?: return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Column {
            // Location name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = state.locationName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        text = weatherDescription(current.weatherCode),
                        fontSize = 14.sp,
                        color = TextMid
                    )
                }
                // Weather icon large
                Text(
                    text = weatherIcon(current.weatherCode),
                    fontSize = 72.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Big temperature
            Text(
                text = "${current.temperature.toInt()}°C",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                letterSpacing = (-2).sp
            )

            Text(
                text = "Real feel  ${current.apparentTemp.toInt()}°C",
                fontSize = 14.sp,
                color = TextMid,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Metric chips row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricChip(
                    icon = Icons.Default.Air,
                    label = "Wind",
                    value = "${current.windSpeed.toInt()} km/h",
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    icon = Icons.Default.Thermostat,
                    label = "Temperature",
                    value = "${current.temperature.toInt()}°C",
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "${current.humidity}%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// Animated Hourly Detail Card
// ─────────────────────────────────────────────
@Composable
fun AnimatedWeatherCard(hour: HourlyWeather, index: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 60L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(350)) + slideInHorizontally(
            initialOffsetX = { it / 3 },
            animationSpec = tween(350, easing = EaseOutCubic)
        )
    ) {
        WeatherCard(hour = hour)
    }
}

// ─────────────────────────────────────────────
// Weather Detail Card
// ─────────────────────────────────────────────
@Composable
fun WeatherCard(hour: HourlyWeather) {
    val isAlert = hour.weatherCode >= 65

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAlert) AlertBg else CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Time column
            Column(modifier = Modifier.width(56.dp)) {
                Text(
                    text = hour.time,
                    color = TextMid,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (isAlert) {
                    Text(
                        text = "⚠ Alert",
                        color = AlertRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Icon + description
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(ChipBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = weatherIcon(hour.weatherCode), fontSize = 22.sp)
                }
                Column {
                    Text(
                        text = weatherDescription(hour.weatherCode),
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "💨 ${hour.windSpeed.toInt()} km/h  💧 ${hour.humidity}%",
                        color = TextLight,
                        fontSize = 11.sp
                    )
                }
            }

            // Temperature
            Text(
                text = "${hour.temperature.toInt()}°",
                color = tempColor(hour.temperature),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─────────────────────────────────────────────
// Metric Chip
// ─────────────────────────────────────────────
@Composable
fun MetricChip(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ChipBg)
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = PrimaryBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = TextLight, fontSize = 11.sp)
        Text(
            text = value,
            color = TextDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────
// Loading
// ─────────────────────────────────────────────
@Composable
fun LoadingIndicator() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PrimaryBlue, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Fetching weather...", color = TextMid, fontSize = 14.sp)
        }
    }
}

// ─────────────────────────────────────────────
// Error
// ─────────────────────────────────────────────
@Composable
fun ErrorCard(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AlertBg)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⚠️", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(message, color = TextDark, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Retry", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Helpers — unchanged
// ─────────────────────────────────────────────
fun weatherIcon(code: Int): String = when (code) {
    0 -> "☀️"
    1, 2 -> "🌤️"
    3 -> "☁️"
    45, 48 -> "🌫️"
    51, 53, 55 -> "🌦️"
    61, 63 -> "🌧️"
    65 -> "🌧️"
    71, 73, 75 -> "❄️"
    77 -> "🌨️"
    80, 81, 82 -> "⛈️"
    85, 86 -> "🌨️"
    95 -> "⛈️"
    96, 99 -> "🌩️"
    else -> "🌡️"
}

fun weatherDescription(code: Int): String = when (code) {
    0 -> "Clear Sky"
    1 -> "Mainly Clear"
    2 -> "Partly Cloudy"
    3 -> "Overcast"
    45 -> "Foggy"
    48 -> "Icy Fog"
    51 -> "Light Drizzle"
    53 -> "Drizzle"
    55 -> "Heavy Drizzle"
    61 -> "Slight Rain"
    63 -> "Moderate Rain"
    65 -> "Heavy Rain"
    71 -> "Light Snow"
    73 -> "Moderate Snow"
    75 -> "Heavy Snow"
    80 -> "Rain Showers"
    81 -> "Heavy Showers"
    82 -> "Violent Showers"
    95 -> "Thunderstorm"
    96, 99 -> "Severe Storm"
    else -> "Unknown"
}

fun tempColor(temp: Double): Color = when {
    temp <= 10 -> Color(0xFF64B5F6)
    temp <= 20 -> PrimaryBlue
    temp <= 28 -> Color(0xFF26A69A)
    temp <= 35 -> Color(0xFFFFA726)
    else -> Color(0xFFEF5350)
}