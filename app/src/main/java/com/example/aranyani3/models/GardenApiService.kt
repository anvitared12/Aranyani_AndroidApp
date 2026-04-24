package com.example.aranyani3.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class CareInfo(
    val plant: String,
    val howToGrow: String,
    val sunlight: String,
    val careRecommendation: String,
)

data class RecommendationsResponse(
    val recommendedPlants: List<String>,
)

object GardenApiService {

    suspend fun getRecommendations(
        baseUrl: String,
        diameter: Float,
        height: Float,
    ): Result<RecommendationsResponse> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$baseUrl/recommendations/?diameter=$diameter&height=$height")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            val code = conn.responseCode
            if (code != 200) {
                return@withContext Result.failure(Exception("Server returned $code"))
            }
            val body = BufferedReader(InputStreamReader(conn.inputStream)).readText()
            val json = JSONObject(body)
            val arr = json.getJSONArray("recommended_plants")
            val plants = (0 until arr.length()).map { arr.getString(it) }.sorted()
            Result.success(RecommendationsResponse(plants))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCare(
        baseUrl: String,
        plantName: String,
    ): Result<List<CareInfo>> = withContext(Dispatchers.IO) {
        try {
            val encodedName = java.net.URLEncoder.encode(plantName, "UTF-8")
            val url = URL("$baseUrl/care/$encodedName")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            val code = conn.responseCode
            if (code == 404) {
                return@withContext Result.success(emptyList())
            }
            if (code != 200) {
                return@withContext Result.failure(Exception("Server returned $code"))
            }
            val body = BufferedReader(InputStreamReader(conn.inputStream)).readText()
            val arr = JSONArray(body)
            val list = (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                CareInfo(
                    plant = obj.optString("Plant", plantName),
                    howToGrow = obj.optString("How to Grow", ""),
                    sunlight = obj.optString("Sunlight", ""),
                    careRecommendation = obj.optString("Care recommendation", ""),
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
