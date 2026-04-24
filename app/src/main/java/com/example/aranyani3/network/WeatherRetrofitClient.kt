package com.example.aranyani3.network

import com.example.aranyani3.models.WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherRetrofitClient {
    private const val w_BASE_URL = "https://api.open-meteo.com/"

    val weatherApi: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(w_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}