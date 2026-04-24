package com.example.aranyani3.models

data class ScanItem(
    val id: String,
    val image_url: String,
    val scan_type: String,   // "plant" | "disease" | "garden"
    val name: String,
    val created_at: String
)