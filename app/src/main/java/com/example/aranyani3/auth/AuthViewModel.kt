package com.example.aranyani3.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val auth0Manager: Auth0Manager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        refreshSession()
    }

    fun refreshSession() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        auth0Manager.hasValidCredentials { hasValid ->
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = hasValid,
                    sessionChecked = true  // ✅ mark as checked
                )
            }
        }
    }

    fun login(activity: Activity) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        auth0Manager.login(activity) { result ->
            viewModelScope.launch {
                _uiState.value = if (result.isSuccess) {
                    _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        errorMessage = null
                    )
                } else {
                    _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Authentication failed"
                    )
                }
            }
        }
    }

    fun logout(activity: Activity) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        auth0Manager.logout(activity) { result ->
            viewModelScope.launch {
                _uiState.value = if (result.isSuccess) {
                    _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        errorMessage = null
                    )
                } else {
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Logout failed"
                    )
                }
            }
        }
    }
}

class AuthViewModelFactory(
    private val auth0Manager: Auth0Manager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(auth0Manager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}