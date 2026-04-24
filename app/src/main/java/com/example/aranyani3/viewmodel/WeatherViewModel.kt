package com.example.aranyani3.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aranyani3.models.HourlyWeather
import com.example.aranyani3.notification.WeatherNotificationManager
import com.example.aranyani3.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.location.Geocoder
import java.util.Locale

data class WeatherUiState(
    val isLoading: Boolean = false,
    val hourlyData: List<HourlyWeather> = emptyList(),
    val locationName: String = "",
    val error: String? = null
)

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository()
    private val notificationManager = WeatherNotificationManager(application)

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState(isLoading = true)
            try {
                val data = repository.getHourlyWeather(lat, lon)
                // Reverse geocode lat/lon → location name
                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                val locationName = try {
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    val address = addresses?.firstOrNull()
                    address?.locality                        // city name
                        ?: address?.subAdminArea             // district/county fallback
                        ?: address?.adminArea                // state fallback
                        ?: "Unknown Location"
                } catch (e: Exception) {
                    "Unknown Location"
                }
                _uiState.value = WeatherUiState(
                    hourlyData = data,
                    locationName = locationName
                )
                data.firstOrNull()?.let { current ->
                    notificationManager.showWeatherNotification(
                        temperature = current.temperature,
                        weatherCode = current.weatherCode,
                        humidity = current.humidity,
                        windSpeed = current.windSpeed
                    )
                }
            } catch (e: Exception) {
                _uiState.value = WeatherUiState(error = "Failed to load weather: ${e.message}")
            }
        }
    }
}
