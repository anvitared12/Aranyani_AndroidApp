package com.example.aranyani3.models

data class ApiResponse(
    val source: String,
    val plant_name: String,
    val confidence: Float? = null,
    val scientific_name: String? = null,
    val score: Float? = null,
    val local_model_confidence: Float? = null
)