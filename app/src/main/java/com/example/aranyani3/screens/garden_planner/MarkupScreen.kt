package com.example.aranyani3.screens.garden_planner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.aranyani3.viewmodel.GardenViewModel
import com.example.aranyani3.viewmodel.MarkupStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkupScreen(
    viewModel: GardenViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    val stepLabel = when (state.markupStep) {
        MarkupStep.CORNERS -> {
            val remaining = 4 - state.cornerPoints.size
            if (remaining > 0) "Tap corner ${state.cornerPoints.size + 1} of 4 (planting zone)"
            else "All 4 corners marked"
        }
        MarkupStep.REF_POINTS -> {
            val remaining = 2 - state.refPoints.size
            if (remaining > 0) "Tap reference point ${state.refPoints.size + 1} of 2"
            else "Both reference points marked"
        }
        MarkupStep.DONE -> "Ready! Tap Next to continue."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mark Area", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.undoLastPoint() }) {
                        Icon(Icons.Default.Undo, contentDescription = "Undo last point")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stepLabel,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = when (state.markupStep) {
                            MarkupStep.CORNERS -> MaterialTheme.colorScheme.primary
                            MarkupStep.REF_POINTS -> MaterialTheme.colorScheme.tertiary
                            MarkupStep.DONE -> MaterialTheme.colorScheme.secondary
                        },
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.resetMarkup() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Reset")
                        }
                        Button(
                            onClick = onNext,
                            enabled = state.markupStep == MarkupStep.DONE,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Next →")
                        }
                    }
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(state.photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Garden photo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coords ->
                        imageSize = coords.size
                        viewModel.setImageDisplaySize(
                            coords.size.width.toFloat(),
                            coords.size.height.toFloat(),
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            viewModel.addMarkupPoint(offset)
                        }
                    },
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val corners = state.cornerPoints
                val refs = state.refPoints

                val cornerColor = Color(0xFF4CAF50)
                val cornerFill = Color(0x884CAF50)
                val refColor = Color(0xFFFF9800)
                val lineColor = Color(0xFF4CAF50)

                if (corners.size >= 2) {
                    val path = Path()
                    path.moveTo(corners[0].x, corners[0].y)
                    for (i in 1 until corners.size) {
                        path.lineTo(corners[i].x, corners[i].y)
                    }
                    if (corners.size == 4) {
                        path.close()
                        drawPath(path, cornerFill)
                    }
                    drawPath(path, lineColor, style = Stroke(width = 3.dp.toPx()))
                }

                val cornerLabels = listOf("TL", "TR", "BR", "BL")
                corners.forEachIndexed { i, pt ->
                    drawCircle(color = cornerColor, radius = 16.dp.toPx(), center = pt)
                    drawCircle(color = Color.White, radius = 16.dp.toPx(), center = pt, style = Stroke(2.dp.toPx()))
                }

                if (refs.size == 2) {
                    drawLine(
                        color = refColor,
                        start = refs[0],
                        end = refs[1],
                        strokeWidth = 3.dp.toPx(),
                    )
                }
                refs.forEach { pt ->
                    drawCircle(color = refColor, radius = 14.dp.toPx(), center = pt)
                    drawCircle(color = Color.White, radius = 14.dp.toPx(), center = pt, style = Stroke(2.dp.toPx()))
                }
            }

            if (state.markupStep == MarkupStep.REF_POINTS || state.markupStep == MarkupStep.DONE) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        text = if (state.markupStep == MarkupStep.REF_POINTS)
                            "Now tap 2 points on your reference object"
                        else "✓ Reference points set",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }
        }
    }
}
