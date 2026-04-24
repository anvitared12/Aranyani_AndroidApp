package com.example.aranyani3.network

import com.example.aranyani3.models.ApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PlantIdentificationService {
    @Multipart
    @POST("identify")
    suspend fun identifyPlant(
        @Part file: MultipartBody.Part
    ): Response<ApiResponse>
}