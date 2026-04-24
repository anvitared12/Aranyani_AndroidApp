package com.example.aranyani3.network

import com.example.aranyani3.models.CareResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CareApiService {
    @GET("care-recommendation")
    suspend fun getCareRecommendation(
        @Query("plant") plant: String
    ): Response<CareResponse>
}