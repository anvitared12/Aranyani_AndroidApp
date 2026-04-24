package com.example.aranyani3.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val sessionChecked: Boolean = false,
    val userEmail: String? = null
)