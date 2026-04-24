package com.example.aranyani3.repository


import com.example.aranyani3.models.HourlyWeather
import com.example.aranyani3.network.WeatherRetrofitClient

class WeatherRepository {
    private val api = WeatherRetrofitClient.weatherApi

    suspend fun getHourlyWeather(lat: Double, lon: Double): List<HourlyWeather> {
        val response = api.getWeather(lat, lon)
        val h = response.hourly

        // Parse "2024-06-01T14:00" → "14:00"
        return h.time.mapIndexed { i, rawTime ->
            val time = rawTime.substringAfter("T")  // e.g. "14:00"
            HourlyWeather(
                time        = time,
                temperature = h.temperature[i],
                apparentTemp = h.apparentTemp[i],
                humidity    = h.humidity[i],
                windSpeed   = h.windSpeed[i],
                weatherCode = h.weatherCode[i]
            )
        }
    }
}