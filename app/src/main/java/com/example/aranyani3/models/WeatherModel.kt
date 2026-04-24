package com.example.aranyani3.models

import com.google.gson.annotations.SerializedName

// ── Open-Meteo API response ──
data class OpenMeteoResponse(
    @SerializedName("hourly") val hourly: HourlyData
)

data class HourlyData(
    @SerializedName("time")              val time: List<String>,
    @SerializedName("temperature_2m")   val temperature: List<Double>,
    @SerializedName("apparent_temperature") val apparentTemp: List<Double>,
    @SerializedName("relative_humidity_2m") val humidity: List<Int>,
    @SerializedName("wind_speed_10m")   val windSpeed: List<Double>,
    @SerializedName("weather_code")     val weatherCode: List<Int>
)

// ── UI model ──
data class HourlyWeather(
    val time: String,
    val temperature: Double,
    val apparentTemp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int
)