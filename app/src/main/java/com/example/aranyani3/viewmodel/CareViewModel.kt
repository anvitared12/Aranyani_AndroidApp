package com.example.aranyani3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aranyani3.models.CareData
import com.example.aranyani3.network.CareRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class CareUiState(
    val isLoading: Boolean = false,
    val result: CareData? = null,
    val error: String? = null
)

class CareViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CareUiState())
    val uiState: StateFlow<CareUiState> = _uiState.asStateFlow()

    fun fetchCareRecommendation(plantName: String) {
        viewModelScope.launch {
            _uiState.value = CareUiState(isLoading = true)
            try {
                val response = withContext(Dispatchers.IO) {
                    CareRetrofitClient.careApi.getCareRecommendation(plantName)
                }
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data.firstOrNull()
                    _uiState.value = CareUiState(result = data)
                } else {
                    val err = response.errorBody()?.string()
                    _uiState.value = CareUiState(error = "Not found (${response.code()}): $err")
                }
            } catch (e: Exception) {
                _uiState.value = CareUiState(error = e.localizedMessage ?: "Unknown error")
            }
        }
    }
}