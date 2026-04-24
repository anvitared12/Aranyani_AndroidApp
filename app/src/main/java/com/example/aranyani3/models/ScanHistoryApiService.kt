package com.example.aranyani3.models

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ScanHistoryApiService {

    @Multipart
    @POST("scans")
    suspend fun saveScan(
        @Part file: MultipartBody.Part?,
        @Part("scan_type") scanType: RequestBody,
        @Part("name") name: RequestBody
    ): Response<ScanItem>

    @GET("scans")
    suspend fun getScans(): Response<List<ScanItem>>

    @DELETE("scans/{id}")
    suspend fun deleteScan(@Path("id") id: String): Response<Unit>
}