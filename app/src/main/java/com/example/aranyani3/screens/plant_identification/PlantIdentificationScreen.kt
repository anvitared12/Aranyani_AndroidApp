package com.example.aranyani3.screens.plant_identification

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.aranyani3.R
import com.example.aranyani3.models.ApiResponse
import com.example.aranyani3.viewmodel.PlantIdentifyViewModel
import com.example.aranyani3.viewmodel.ScanHistoryViewModel
import com.google.accompanist.permissions.*
import java.io.File

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PlantIdentifyScreen(
    viewModel: PlantIdentifyViewModel = viewModel(),
    onBack: () -> Unit = {},
    scanHistoryViewModel: ScanHistoryViewModel,
    onCareRecommendation: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    fun createCameraUri(): Uri {
        val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(imagesDir, "camera_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val permissions = buildList {
        add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    val permissionState = rememberMultiplePermissionsState(permissions)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImage(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri?.let { viewModel.setSelectedImage(it) }
        } else {
            cameraImageUri = null
        }
    }

    // ── Root Box: background image fills entire screen ──
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image (place background2.png/jpg in res/drawable/)
        Image(
            painter = painterResource(id = R.drawable.download6),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Plant Identifier") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Image Preview
                if (uiState.selectedImageUri != null) {
                    AsyncImage(
                        model = uiState.selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x55000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No image selected", color = Color.White)
                    }
                }

                // Gallery / Camera buttons
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            if (permissionState.allPermissionsGranted) {
                                galleryLauncher.launch("image/*")
                            } else {
                                permissionState.launchMultiplePermissionRequest()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A7A2F))
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Gallery")
                    }

                    Button(
                        onClick = {
                            if (permissionState.allPermissionsGranted) {
                                val uri = createCameraUri()
                                cameraImageUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                permissionState.launchMultiplePermissionRequest()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A7A2F))
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Camera")
                    }
                }

                // Identify Button
                Button(
                    onClick = {
                        viewModel.identifyPlant(context) { uri, name ->
                            scanHistoryViewModel.saveScan(
                                context = context,
                                imageUri = uri,
                                scanType = "plant",
                                name = name
                            )
                        }
                    },
                    enabled = uiState.selectedImageUri != null && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A7A2F))
                ) {
                    Text("Identify Plant")
                }

                // Loading
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                    Text("Identifying plant...", color = Color.White)
                }

                // Error
                uiState.error?.let {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "❌ $it",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Result
                uiState.result?.let { result ->
                    PlantResultCard(
                        result = result,
                        onCareRecommendation = onCareRecommendation
                    )
                }
            }
        }
    }
}

@Composable
fun PlantResultCard(
    result: ApiResponse,
    onCareRecommendation: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "🌱 ${result.plant_name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            result.confidence?.let {
                Text("Confidence: ${"%.1f".format(it * 100)}%")
            }
            result.scientific_name?.let {
                Text("Scientific Name: $it")
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { onCareRecommendation(result.plant_name) },
                modifier = Modifier.fillMaxWidth(),                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4A7A2F)
                )
            ) {
                Text("🌿 Care Recommendation")
            }
        }
    }
}