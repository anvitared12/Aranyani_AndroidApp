package com.example.aranyani3.models

import com.google.gson.annotations.SerializedName

data class CareResponse(
    val status: String,
    val count: Int,
    val data: List<CareData>
)

data class CareData(
    @SerializedName("Plant Name")         val plantName: String? = null,
    @SerializedName("Growth")             val growth: String? = null,
    @SerializedName("Soil")               val soil: String? = null,
    @SerializedName("Sunlight")           val sunlight: String? = null,
    @SerializedName("Watering")           val watering: String? = null,
    @SerializedName("Fertilization Type") val fertilizationType: String? = null
)