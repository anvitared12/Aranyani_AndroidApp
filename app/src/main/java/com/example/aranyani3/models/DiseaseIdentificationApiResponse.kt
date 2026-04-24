package com.example.aranyani3.models

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

data class DiseaseResult(
    val disease_name: String? = null,
    val disease: String? = null,
    val name: String? = null,
    val label: String? = null,
    val prediction: String? = null,
    val confidence: Double? = null,
    val score: Double? = null,
    val description: String? = null,
    val cure: String? = null,
    val treatment: String? = null
) {
    fun displayName(): String =
        disease_name ?: disease ?: name ?: label ?: prediction ?: "Unknown"

    fun displayConfidence(): Double? = confidence ?: score
}

interface DiseaseApi {
    @Multipart
    @POST("detect")
    suspend fun detect(@Part file: MultipartBody.Part): DiseaseResult
}

object DiseaseApiClient {
    private const val BASE_URL = "https://diseaseidentification-backend.onrender.com/"

    val api: DiseaseApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DiseaseApi::class.java)
    }
}