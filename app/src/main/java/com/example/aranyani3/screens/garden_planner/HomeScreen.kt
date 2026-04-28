package com.example.aranyani3.screens.garden_planner

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import android.util.Log
import android.widget.Toast
import com.example.aranyani3.R
import com.example.aranyani3.viewmodel.GardenViewModel
import java.io.File

private const val TAG = "GardenHome"

// ── Theme colors matching Home Screen ──
private val GreenButton  = Color(0xFF4A7A2F)
private val CardBg       = Color(0x33FFFFFF)   // translucent white like PlantIdentifyScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GardenViewModel,
    onPhotoTaken: () -> Unit,
    onSettingsClick: () -> Unit,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var tempUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        Log.d(TAG, "Camera result success=$success uri=$tempUri")
        val uri = tempUri
        if (success && uri != null) {
            viewModel.setPhotoUri(uri)
            onPhotoTaken()
        } else {
            Toast.makeText(context, "Camera capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    // ── Root Box: background image fills entire screen ──
    Box(modifier = Modifier.fillMaxSize()) {

        // Background image
        Image(
            painter = painterResource(id = R.drawable.homebackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Garden Planner", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {

                // Icon circle — translucent white
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0x44FFFFFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFlorist,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp),
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Garden Planner",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    ),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Point your camera at the area where you want to grow plants and calculate how many pots will fit.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color(0xCCFFFFFF),   // slightly transparent white
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Steps card — translucent white like image preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        StepRow(number = 1, text = "Take a photo of your garden area")
                        StepRow(number = 2, text = "Tap 4 corners of the planting zone")
                        StepRow(number = 3, text = "Mark 2 points on a reference object (e.g. A4 sheet = 29.7 cm)")
                        StepRow(number = 4, text = "Enter pot size and reference length")
                        StepRow(number = 5, text = "See how many pots fit and which plants to grow!")
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Open Camera button — green
                Button(
                    onClick = {
                        val uri = createTempImageUri(context)
                        tempUri = uri
                        launcher.launch(uri)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenButton)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Open Camera",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun StepRow(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFF4A7A2F)),   // green circle number
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$number",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
        )
    }
}

private fun createTempImageUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "images").also { it.mkdirs() }
    val file = File(imagesDir, "garden_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}