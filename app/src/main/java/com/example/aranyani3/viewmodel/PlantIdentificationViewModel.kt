package com.example.aranyani3.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aranyani3.models.ApiResponse
import com.example.aranyani3.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

data class PlantIdentifyUiState(
    val selectedImageUri: Uri? = null,
    val isLoading: Boolean = false,
    val result: ApiResponse? = null,
    val error: String? = null
)

class PlantIdentifyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlantIdentifyUiState())
    val uiState: StateFlow<PlantIdentifyUiState> = _uiState.asStateFlow()

    fun setSelectedImage(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri,
            result = null,
            error = null
        )
    }

    fun clearSelection() {
        _uiState.value = PlantIdentifyUiState()
    }

    fun identifyPlant(context: Context, onSuccess: ((Uri, String) -> Unit)? = null) {
        val uri = _uiState.value.selectedImageUri ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                result = null
            )

            try {
                val response = withContext(Dispatchers.IO) {
                    val file = uriToFile(context, uri)
                    val mimeType = getMimeType(context.contentResolver, uri) ?: "image/jpeg"
                    val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
                    RetrofitClient.plantIdentifyApi.identifyPlant(multipartBody)
                }

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        result = response.body()
                    )
                    // ✅ Notify the screen so IT can call saveScan
                    val plantName = response.body()?.plant_name ?: "Unknown Plant"
                    onSuccess?.invoke(uri, plantName)

                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed ${response.code()}"
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

    private fun uriToFile(context: Context, uri: Uri): File {
        val extension = getMimeType(context.contentResolver, uri)?.substringAfter("/") ?: "jpg"
        val fileName = "plant_${System.currentTimeMillis()}.$extension"
        val file = File(context.cacheDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    private fun getMimeType(contentResolver: ContentResolver, uri: Uri): String? {
        return contentResolver.getType(uri)
            ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            )
    }
}