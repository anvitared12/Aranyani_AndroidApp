package com.example.aranyani3.screens.garden_planner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aranyani3.viewmodel.GardenViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: GardenViewModel,
    onBack: () -> Unit,
    onSeePlants: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val result = state.calculationResult

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Results") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {

            if (result == null) {
                Text("No results yet. Please go back and complete the process.")
                return@Column
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${result.totalPots}",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "pots fit in your area",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Area Measurements",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    MeasurementRow("Floor Width", "${String.format("%.1f", result.floorWidthCm)} cm")
                    MeasurementRow("Floor Height", "${String.format("%.1f", result.floorHeightCm)} cm")
                    MeasurementRow("Pot Diameter", "${state.potDiameterCm} cm")
                    MeasurementRow("Pots along width", "${result.potsAlongWidth}")
                    MeasurementRow("Pots along height", "${result.potsAlongHeight}")
                }
            }

            if (result.totalPots > 0) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Pot Layout Preview",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val potColor = MaterialTheme.colorScheme.primary
                        val potOutline = MaterialTheme.colorScheme.outline
                        val cols = result.potsAlongWidth
                        val rows = result.potsAlongHeight
                        val maxCols = min(cols, 20)
                        val maxRows = min(rows, 20)
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                        ) {
                            val canvasW = size.width
                            val canvasH = size.height
                            val cellW = canvasW / maxCols
                            val cellH = canvasH / maxRows
                            val r = (min(cellW, cellH) / 2f) * 0.85f

                            for (row in 0 until maxRows) {
                                for (col in 0 until maxCols) {
                                    val cx = cellW * col + cellW / 2f
                                    val cy = cellH * row + cellH / 2f
                                    drawCircle(
                                        color = potColor.copy(alpha = 0.3f),
                                        radius = r,
                                        center = Offset(cx, cy),
                                    )
                                    drawCircle(
                                        color = potColor,
                                        radius = r,
                                        center = Offset(cx, cy),
                                        style = Stroke(width = 2.dp.toPx()),
                                    )
                                }
                            }

                            if (cols > maxCols || rows > maxRows) {
                                drawRect(
                                    color = Color.Black.copy(alpha = 0.1f),
                                    topLeft = Offset(0f, 0f),
                                    size = Size(canvasW, canvasH),
                                )
                            }
                        }
                        if (cols > maxCols || rows > maxRows) {
                            Text(
                                "(Preview shows up to 20×20 of ${cols}×${rows} pots)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSeePlants,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Icon(Icons.Default.LocalFlorist, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Plants You Can Grow",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Composable
private fun MeasurementRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
    }
}
