package com.example.aranyani3.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aranyani3.auth.Auth0Manager
import com.example.aranyani3.models.ScanItem
import com.example.aranyani3.network.ScanHistoryRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume

data class ScanHistoryUiState(
    val scans: List<ScanItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ScanHistoryViewModel(
    private val auth0Manager: Auth0Manager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanHistoryUiState())
    val uiState: StateFlow<ScanHistoryUiState> = _uiState.asStateFlow()

    fun saveScan(
        context: Context,
        imageUri: Uri?,          // ✅ restored, nullable so garden scan with no photo is safe
        scanType: String,        // "plant" | "disease" | "garden"
        name: String
    ) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: return@launch
                val api = ScanHistoryRetrofitClient.create(token)

                val scanTypePart = scanType.toRequestBody("text/plain".toMediaTypeOrNull())
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())

                // ✅ only upload image if uri is available
                val filePart: MultipartBody.Part? = imageUri?.let { uri ->
                    val file = uriToFile(context, uri)
                    val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", file.name, requestBody)
                }

                withContext(Dispatchers.IO) {
                    api.saveScan(filePart, scanTypePart, namePart)
                }

                loadScans()
            } catch (e: Exception) {
                // silently fail — scan result already shown to user
            }
        }
    }

    fun loadScans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val token = getToken()
            Log.d("AUTH_DEBUG", "Token: $token")

            try {
                if (token == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Token is NULL")
                    return@launch
                }
                val api = ScanHistoryRetrofitClient.create(token)
                val response = withContext(Dispatchers.IO) { api.getScans() }
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        scans = response.body() ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Unknown error"
                )
            }
        }
    }

    fun deleteScan(id: String) {
        viewModelScope.launch {
            try {
                val token = getToken() ?: return@launch
                val api = ScanHistoryRetrofitClient.create(token)
                withContext(Dispatchers.IO) { api.deleteScan(id) }
                _uiState.value = _uiState.value.copy(
                    scans = _uiState.value.scans.filter { it.id != id }
                )
            } catch (e: Exception) { /* ignore */ }
        }
    }

    private suspend fun getToken(): String? = suspendCancellableCoroutine { cont ->
        auth0Manager.getAccessToken { result ->
            cont.resume(result.getOrNull())
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { input.copyTo(it) }
        }
        return file
    }
}