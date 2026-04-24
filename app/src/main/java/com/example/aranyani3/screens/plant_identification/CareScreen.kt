package com.example.aranyani3.screens.plant_identification

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aranyani3.viewmodel.CareViewModel
import com.example.aranyani3.models.CareData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareScreen(
    plantName: String,
    onBack: () -> Unit = {},
    viewModel: CareViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(plantName) {
        viewModel.fetchCareRecommendation(plantName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Care Recommendation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2D4A1E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Plant name header
            Text(
                text = "🌿 $plantName",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D4A1E)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF4A7A2F))
                            Spacer(Modifier.height(12.dp))
                            Text("Fetching care tips...", color = Color(0xFF4A7A2F))
                        }
                    }
                }

                uiState.error != null -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "❌ ${uiState.error}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                uiState.result != null -> {
                    CareDetailCard(uiState.result!!)
                }
            }
        }
    }
}

@Composable
fun CareDetailCard(care: CareData) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CareRow(emoji = "🌱", label = "Growth", value = care.growth)
        CareRow(emoji = "🪨", label = "Soil",          value = care.soil)
        CareRow(emoji = "☀️", label = "Sunlight",      value = care.sunlight)
        CareRow(emoji = "💧", label = "Watering",      value = care.watering)
        CareRow(emoji = "🧪", label = "Fertilization", value = care.fertilizationType)
    }
}

@Composable
fun CareRow(emoji: String, label: String, value: String?) {
    if (value.isNullOrBlank()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF0D6))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(emoji, fontSize = 24.sp)
            Column {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A7A2F)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = Color(0xFF2D4A1E)
                )
            }
        }
    }
}