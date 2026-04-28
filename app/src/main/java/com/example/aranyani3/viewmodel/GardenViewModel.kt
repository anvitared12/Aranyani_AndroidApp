package com.example.aranyani3.viewmodel

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aranyani3.models.CareInfo
import com.example.aranyani3.models.GardenApiService
import com.example.aranyani3.utils.PerspectiveUtils
import com.example.aranyani3.utils.PotCalculationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val DEFAULT_API_URL = "http://13.233.183.118:8001"

data class GardenUiState(
    val photoUri: Uri? = null,
    val imageDisplayWidth: Float = 0f,
    val imageDisplayHeight: Float = 0f,
    val cornerPoints: List<Offset> = emptyList(),
    val refPoints: List<Offset> = emptyList(),
    val markupStep: MarkupStep = MarkupStep.CORNERS,
    val potDiameterCm: String = "",
    val potHeightCm: String = "30",
    val refLengthCm: String = "",
    val calculationResult: PotCalculationResult? = null,
    val plants: List<String> = emptyList(),
    val isLoadingPlants: Boolean = false,
    val plantsError: String? = null,
    val careInfoList: List<CareInfo> = emptyList(),
    val isLoadingCare: Boolean = false,
    val careError: String? = null,
    val selectedPlant: String = "",
    val apiBaseUrl: String = DEFAULT_API_URL,
)

enum class MarkupStep { CORNERS, REF_POINTS, DONE }

class GardenViewModel : ViewModel() {

    private val _state = MutableStateFlow(GardenUiState())
    val state: StateFlow<GardenUiState> = _state.asStateFlow()

    fun setPhotoUri(uri: Uri?) {
        _state.update {
            it.copy(
                photoUri = uri,
                cornerPoints = emptyList(),
                refPoints = emptyList(),
                markupStep = MarkupStep.CORNERS,
                calculationResult = null,
            )
        }
    }

    fun setImageDisplaySize(w: Float, h: Float) {
        _state.update { it.copy(imageDisplayWidth = w, imageDisplayHeight = h) }
    }

    fun addMarkupPoint(offset: Offset) {
        val s = _state.value
        when (s.markupStep) {
            MarkupStep.CORNERS -> {
                if (s.cornerPoints.size < 4) {
                    val newCorners = s.cornerPoints + offset
                    _state.update {
                        it.copy(
                            cornerPoints = newCorners,
                            markupStep = if (newCorners.size == 4) MarkupStep.REF_POINTS else MarkupStep.CORNERS,
                        )
                    }
                }
            }
            MarkupStep.REF_POINTS -> {
                if (s.refPoints.size < 2) {
                    val newRef = s.refPoints + offset
                    _state.update {
                        it.copy(
                            refPoints = newRef,
                            markupStep = if (newRef.size == 2) MarkupStep.DONE else MarkupStep.REF_POINTS,
                        )
                    }
                }
            }
            MarkupStep.DONE -> {}
        }
    }

    fun undoLastPoint() {
        val s = _state.value
        when {
            s.refPoints.isNotEmpty() -> _state.update {
                it.copy(refPoints = it.refPoints.dropLast(1), markupStep = MarkupStep.REF_POINTS)
            }
            s.cornerPoints.isNotEmpty() -> _state.update {
                it.copy(cornerPoints = it.cornerPoints.dropLast(1), markupStep = MarkupStep.CORNERS)
            }
        }
    }

    fun resetMarkup() {
        _state.update {
            it.copy(
                cornerPoints = emptyList(),
                refPoints = emptyList(),
                markupStep = MarkupStep.CORNERS,
            )
        }
    }

    fun setPotDiameter(v: String) = _state.update { it.copy(potDiameterCm = v) }
    fun setPotHeight(v: String)   = _state.update { it.copy(potHeightCm = v) }
    fun setRefLength(v: String)   = _state.update { it.copy(refLengthCm = v) }

    fun setApiBaseUrl(v: String) {
        _state.update { it.copy(apiBaseUrl = v.trimEnd('/')) }
    }

    fun calculate(): Boolean {
        val s = _state.value
        val diameter = s.potDiameterCm.toFloatOrNull() ?: return false
        val refLen   = s.refLengthCm.toFloatOrNull()   ?: return false
        if (s.cornerPoints.size != 4 || s.refPoints.size != 2) return false
        return try {
            val result = PerspectiveUtils.calculate(
                corners       = s.cornerPoints,
                refPoints     = s.refPoints,
                refLengthCm   = refLen,
                potDiameterCm = diameter,
            )
            _state.update { it.copy(calculationResult = result) }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun loadPlants() {
        val s = _state.value
        val diameter = s.potDiameterCm.toFloatOrNull() ?: run {
            android.util.Log.w("GardenVM", "loadPlants skipped: potDiameterCm='${s.potDiameterCm}'")
            return
        }
        val height = s.potHeightCm.toFloatOrNull() ?: run {
            android.util.Log.w("GardenVM", "loadPlants skipped: potHeightCm='${s.potHeightCm}'")
            return
        }

        _state.update { it.copy(isLoadingPlants = true, plantsError = null, plants = emptyList()) }

        viewModelScope.launch {
            GardenApiService.getRecommendations(s.apiBaseUrl, diameter, height).fold(
                onSuccess = { r ->
                    _state.update { it.copy(plants = r.recommendedPlants, isLoadingPlants = false) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoadingPlants = false,
                            plantsError = "Could not reach the API.\n\n" +
                                    "• Make sure the backend is deployed and running.\n" +
                                    "• Check the API URL in Settings (currently: ${s.apiBaseUrl}).\n\n" +
                                    "Error: ${e.message}",
                        )
                    }
                },
            )
        }
    }

    fun loadCare(plantName: String) {
        val s = _state.value
        _state.update {
            it.copy(
                selectedPlant = plantName,
                isLoadingCare = true,
                careError     = null,
                careInfoList  = emptyList(),
            )
        }
        viewModelScope.launch {
            GardenApiService.getCare(s.apiBaseUrl, plantName).fold(
                onSuccess = { list ->
                    _state.update { it.copy(careInfoList = list, isLoadingCare = false) }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            isLoadingCare = false,
                            careError = "Failed to load care info: ${e.message}",
                        )
                    }
                },
            )
        }
    }
}