package com.example.aranyani3.screens.garden_planner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext                          // ✅
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aranyani3.viewmodel.GardenViewModel
import com.example.aranyani3.viewmodel.ScanHistoryViewModel              // ✅

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementInputScreen(
    viewModel: GardenViewModel,
    scanHistoryViewModel: ScanHistoryViewModel,                          // ✅
    onBack: () -> Unit,
    onCalculate: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current                                   // ✅

    val isValid = state.potDiameterCm.toFloatOrNull()?.let { it > 0 } == true &&
            state.refLengthCm.toFloatOrNull()?.let { it > 0 } == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Measurements") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {

            Text(
                text = "Enter measurements",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "These values help calculate how many pots fit in your marked area.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider()

            SectionHeader(title = "Pot Details")

            OutlinedTextField(
                value = state.potDiameterCm,
                onValueChange = viewModel::setPotDiameter,
                label = { Text("Pot Diameter (cm)") },
                supportingText = { Text("The outer diameter of your pot. E.g. 20") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.potDiameterCm.isNotEmpty() && state.potDiameterCm.toFloatOrNull() == null,
            )

            OutlinedTextField(
                value = state.potHeightCm,
                onValueChange = viewModel::setPotHeight,
                label = { Text("Pot Height (cm)") },
                supportingText = { Text("Used to filter suitable plants. E.g. 30") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            HorizontalDivider()

            SectionHeader(title = "Reference Object")

            OutlinedTextField(
                value = state.refLengthCm,
                onValueChange = viewModel::setRefLength,
                label = { Text("Reference Object Length (cm)") },
                supportingText = { Text("Length of the object you marked. A4 paper = 29.7 cm, A4 width = 21 cm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = state.refLengthCm.isNotEmpty() && state.refLengthCm.toFloatOrNull() == null,
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Common reference lengths",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ReferenceRow("A4 paper (long side)", "29.7 cm") {
                        viewModel.setRefLength("29.7")
                    }
                    ReferenceRow("A4 paper (short side)", "21.0 cm") {
                        viewModel.setRefLength("21.0")
                    }
                    ReferenceRow("Letter paper (long side)", "27.9 cm") {
                        viewModel.setRefLength("27.9")
                    }
                    ReferenceRow("30 cm ruler", "30.0 cm") {
                        viewModel.setRefLength("30.0")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val success = viewModel.calculate()                  // ✅
                    if (success) {
                        val s = state
                        scanHistoryViewModel.saveScan(                   // ✅
                            context  = context,
                            imageUri = s.photoUri,
                            scanType = "garden",
                            name     = "Pot ${s.potDiameterCm}cm × ${s.potHeightCm}cm | Ref ${s.refLengthCm}cm",
                        )
                        onCalculate()                                    // ✅ navigate only after success
                    }
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    text = "Calculate Pots",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun ReferenceRow(label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)) {
            Text(value, style = MaterialTheme.typography.bodySmall)
        }
    }
}