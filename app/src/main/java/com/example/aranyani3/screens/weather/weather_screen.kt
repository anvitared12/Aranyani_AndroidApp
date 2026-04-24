package com.example.aranyani3.screens.weather

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
// Color Palette
// ─────────────────────────────────────────────
val SkyTop    = Color(0xFF0D1B3E)
val SkyMid    = Color(0xFF1A3A6B)
val CardBg    = Color(0xFF1E2D50)
val CardBorder = Color(0xFF2E4A80)
val AccentBlue = Color(0xFF4FC3F7)
val AccentGold = Color(0xFFFFD54F)
val TextPrimary = Color(0xFFE8F4FD)
val TextSecondary = Color(0xFF8BAAC8)

// ─────────────────────────────────────────────
// Weather Screen
// ─────────────────────────────────────────────
@Composable
fun WeatherScreen(
    latitude: Double = 12.9716,   // Default: Bengaluru
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(SkyTop, SkyMid, Color(0xFF0A1628))
                )
            )
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
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero current weather
        item {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                CurrentWeatherHero(state)
            }
        }

        // Section label
        item {
            Text(
                text = "24-Hour Forecast",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        // Hourly forecast cards
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(28.dp), ambientColor = AccentBlue.copy(0.3f)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1B3A6B), Color(0xFF0D2040))
                    )
                )
                .border(1.dp, CardBorder, RoundedCornerShape(28.dp))
                .padding(28.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = weatherIcon(current.weatherCode),
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = state.locationName,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.5.sp
                        )

                        Text(
                            text = "${current.temperature.toInt()}°C",
                            fontSize = 52.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = weatherDescription(current.weatherCode),
                            fontSize = 16.sp,
                            color = AccentBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = CardBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MetricChip(icon = Icons.Default.Air, label = "Wind", value = "${current.windSpeed.toInt()} km/h")
                    MetricChip(icon = Icons.Default.WaterDrop, label = "Humidity", value = "${current.humidity}%")
                    MetricChip(icon = Icons.Default.Visibility, label = "Feels like", value = "${current.apparentTemp.toInt()}°")
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Animated Hourly Card
// ─────────────────────────────────────────────
@Composable
fun AnimatedWeatherCard(hour: HourlyWeather, index: Int) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * 80L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInHorizontally(
            initialOffsetX = { it / 2 },
            animationSpec = tween(400, easing = EaseOutCubic)
        )
    ) {
        WeatherCard(hour = hour)
    }
}

// ─────────────────────────────────────────────
// Weather Card
// ─────────────────────────────────────────────
@Composable
fun WeatherCard(hour: HourlyWeather) {
    val isAlert = hour.weatherCode >= 65  // Rain/storm threshold

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isAlert) 12.dp else 6.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = if (isAlert) Color(0xFFFF6B6B).copy(0.3f) else AccentBlue.copy(0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = if (isAlert)
                            listOf(Color(0xFF3D1515), Color(0xFF251428))
                        else
                            listOf(CardBg, Color(0xFF162238))
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = if (isAlert)
                            listOf(Color(0xFFFF6B6B).copy(0.5f), Color(0xFFFF8E53).copy(0.3f))
                        else
                            listOf(CardBorder.copy(0.6f), CardBorder.copy(0.2f))
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Time
                Column(modifier = Modifier.width(60.dp)) {
                    Text(
                        text = hour.time,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (isAlert) {
                        Text(
                            text = "⚠ Alert",
                            color = Color(0xFFFF8E53),
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
                    Text(text = weatherIcon(hour.weatherCode), fontSize = 32.sp)
                    Column {
                        Text(
                            text = weatherDescription(hour.weatherCode),
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "💨 ${hour.windSpeed.toInt()} km/h",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Temperature
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${hour.temperature.toInt()}°C",
                        color = tempColor(hour.temperature),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "💧 ${hour.humidity}%",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Metric chip
// ─────────────────────────────────────────────
@Composable
fun MetricChip(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, tint = AccentBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
}

// ─────────────────────────────────────────────
// Loading
// ─────────────────────────────────────────────
@Composable
fun LoadingIndicator() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = AccentBlue, strokeWidth = 3.dp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Fetching weather...", color = TextSecondary, fontSize = 14.sp)
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3D1515))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⚠️", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(message, color = TextPrimary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Retry", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Helpers
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
    temp <= 10 -> Color(0xFF81D4FA)
    temp <= 20 -> AccentBlue
    temp <= 28 -> Color(0xFF80CBC4)
    temp <= 35 -> AccentGold
    else -> Color(0xFFFF8A65)
}