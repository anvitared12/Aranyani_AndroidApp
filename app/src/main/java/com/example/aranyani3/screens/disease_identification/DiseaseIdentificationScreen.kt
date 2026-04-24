package com.example.aranyani3.screens.disease_identification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.aranyani3.models.DiseaseApiClient
import com.example.aranyani3.models.DiseaseResult
import com.example.aranyani3.viewmodel.ScanHistoryViewModel
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private const val TAG = "DiseaseDetection"

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun DiseaseDetectionScreen(
    onAddCureClick: (String) -> Unit = {},
    scanHistoryViewModel: ScanHistoryViewModel  // ✅ NEW
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<DiseaseResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val cameraImageFile = remember {
        File(context.cacheDir, "disease_capture.jpg")
    }

    val cameraImageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            cameraImageFile
        )
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            imageBitmap = loadBitmapSafe(context, uri)
            result = null
            errorMessage = null
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        Log.d(TAG, "Camera result: success=$success, fileSize=${cameraImageFile.length()}")
        if (success && cameraImageFile.exists() && cameraImageFile.length() > 0) {
            val bmp = loadBitmapFromFile(cameraImageFile)
            if (bmp != null) {
                imageBitmap = bmp
                imageUri = cameraImageUri
                result = null
                errorMessage = null
            } else {
                errorMessage = "Failed to load captured image. Please try again."
            }
        } else {
            if (!success) errorMessage = "Camera capture cancelled."
            else errorMessage = "Camera produced an empty file. Please try again."
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            if (cameraImageFile.exists()) cameraImageFile.delete()
            cameraImageFile.createNewFile()
            cameraLauncher.launch(cameraImageUri)
        } else {
            errorMessage = "Camera permission denied."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Disease Identification",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Image preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val bmp = imageBitmap
                if (bmp != null) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Selected leaf",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = "Pick or capture an image of the affected leaf",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Camera")
            }

            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Gallery")
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val uri = imageUri ?: return@Button
                scope.launch {
                    isLoading = true
                    errorMessage = null
                    result = null
                    try {
                        val response = withContext(Dispatchers.IO) {
                            val part = if (uri == cameraImageUri && cameraImageFile.exists()) {
                                fileToMultipart(cameraImageFile)
                            } else {
                                uriToMultipart(context, uri)
                            }
                            Log.d(TAG, "Uploading: ${part.body.contentLength()} bytes")
                            DiseaseApiClient.api.detect(part)
                        }
                        Log.d(TAG, "Response: $response")
                        result = response

                        // ✅ NEW — save to scan history after successful detection
                        scanHistoryViewModel.saveScan(
                            context = context,
                            imageUri = uri,
                            scanType = "disease",
                            name = response.displayName()
                        )

                    } catch (e: retrofit2.HttpException) {
                        val errorBody = try {
                            e.response()?.errorBody()?.string()
                        } catch (ex: Exception) { null }

                        if (e.code() == 404) {
                            val detail = errorBody
                                ?.substringAfter("\"detail\":\"", "")
                                ?.substringBefore("\"", "")
                                ?.takeIf { it.isNotEmpty() }
                            errorMessage = detail
                                ?: "Could not identify disease. Try a clearer, closer photo of the leaf."
                        } else {
                            errorMessage = when (e.code()) {
                                422 -> "Validation error: $errorBody"
                                500 -> "Server error: $errorBody"
                                else -> "HTTP ${e.code()}: $errorBody"
                            }
                        }
                        Log.e(TAG, "HttpException ${e.code()}: $errorBody", e)
                    } catch (e: com.google.gson.JsonSyntaxException) {
                        errorMessage = "Unexpected response format from server."
                        Log.e(TAG, "JSON parse error", e)
                    } catch (e: java.net.SocketTimeoutException) {
                        errorMessage = "Request timed out. Server may be waking up — please try again."
                        Log.e(TAG, "Timeout", e)
                    } catch (e: java.io.IOException) {
                        errorMessage = "Network error: ${e.message}"
                        Log.e(TAG, "IOException", e)
                    } catch (e: Exception) {
                        errorMessage = "Unexpected error [${e.javaClass.simpleName}]: ${e.message}"
                        Log.e(TAG, "Unknown error", e)
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = imageUri != null && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text("Detecting…")
            } else {
                Text("Detect Disease")
            }
        }

        Spacer(Modifier.height(20.dp))

        errorMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        result?.let { res ->
            ResultCard(
                result = res,
                onAddCureClick = { onAddCureClick(res.displayName()) }
            )
        }
    }
}

@Composable
private fun ResultCard(
    result: DiseaseResult,
    onAddCureClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Detection Result",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = result.displayName(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            result.displayConfidence()?.let { conf ->
                Spacer(Modifier.height(4.dp))
                val pct = if (conf <= 1.0) conf * 100 else conf
                Text(
                    text = "Confidence: ${"%.1f".format(pct)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            result.description?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            (result.cure ?: result.treatment)?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Recommended treatment:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onAddCureClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Add Cure for Disease")
            }
        }
    }
}

private fun loadBitmapSafe(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        Log.e(TAG, "loadBitmapSafe failed: ${e.message}", e)
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "loadBitmapSafe stream fallback failed: ${ex.message}", ex)
            null
        }
    }
}

private fun loadBitmapFromFile(file: File): Bitmap? {
    return try {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        BitmapFactory.decodeFile(file.absolutePath, options)
    } catch (e: Exception) {
        Log.e(TAG, "loadBitmapFromFile failed: ${e.message}", e)
        null
    }
}

private fun fileToMultipart(file: File): MultipartBody.Part {
    if (file.length() == 0L) throw IllegalStateException("Camera file is empty.")
    val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("file", "leaf.jpg", requestBody)
}

private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part {
    val resolver = context.contentResolver
    val mimeType = resolver.getType(uri) ?: "image/jpeg"
    val extension = when (mimeType) {
        "image/png" -> ".png"
        "image/webp" -> ".webp"
        else -> ".jpg"
    }
    val tempFile = File.createTempFile("upload_", extension, context.cacheDir)
    resolver.openInputStream(uri)?.use { input ->
        tempFile.outputStream().use { output -> input.copyTo(output) }
    }
    if (tempFile.length() == 0L) throw IllegalStateException("Gallery image file is empty.")
    val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("file", "leaf$extension", requestBody)
}