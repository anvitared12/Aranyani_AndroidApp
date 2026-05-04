package com.example.aranyani3

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aranyani2.ui_screens.screens.MainHome
import com.example.aranyani3.auth.Auth0Manager
import com.example.aranyani3.auth.AuthViewModel
import com.example.aranyani3.auth.AuthViewModelFactory
import com.example.aranyani3.screens.ReminderScreen.PlantReminderScreen
import com.example.aranyani3.screens.auth.LoginScreen
import com.example.aranyani3.screens.disease_identification.DiseaseDetectionScreen
import com.example.aranyani3.screens.entry_dashboard.Entry_Dashboard2
import com.example.aranyani3.screens.entry_dashboard.Entry_Dashboard3
import com.example.aranyani3.screens.entry_dashboard.Entry_Dashboard4
import com.example.aranyani3.screens.entry_dashboard.FirstScreen
import com.example.aranyani3.screens.garden_planner.HomeScreen
import com.example.aranyani3.screens.garden_planner.MarkupScreen
import com.example.aranyani3.screens.garden_planner.MeasurementInputScreen
import com.example.aranyani3.screens.garden_planner.PlantsScreen
import com.example.aranyani3.screens.garden_planner.ResultScreen
import com.example.aranyani3.screens.plant_identification.CareScreen
import com.example.aranyani3.screens.plant_identification.PlantIdentifyScreen
import com.example.aranyani3.screens.sustainable.SustainableGardenScreen
import com.example.aranyani3.ui.theme.Aranyani3Theme
import com.example.aranyani3.viewmodel.GardenViewModel
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import androidx.compose.runtime.remember
import androidx.navigation.navArgument
import com.example.aranyani3.screens.ReminderScreen.CompostReminderScreen
import com.example.aranyani3.screens.disease_identification.DiseaseCureScreen
import com.example.aranyani3.viewmodel.ScanHistoryViewModel

class MainActivity : ComponentActivity() {

    // Handles POST_NOTIFICATIONS permission result (Android 13+)
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Optional: show a snackbar/dialog if !isGranted explaining why notifications matter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel(this)
        requestNotificationPermissionIfNeeded()
        setContent {
            Aranyani3Theme {
                AppNavigation()
            }
        }
    }

    private fun requestBatteryOptimizationExemption() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }
    }


    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

// ── Notification channel (safe to call multiple times — system ignores duplicates) ──

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "plant_channel",
            "Plant Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders to water your plants"
            enableLights(true)
            enableVibration(true)
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

// ── Navigation ────────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context       = LocalContext.current
    val activity      = context as? Activity

    val auth0Manager = remember { Auth0Manager(context) }
    val scanHistoryViewModel = remember { ScanHistoryViewModel(auth0Manager) }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(auth0Manager = auth0Manager)  // ✅ reuse auth0Manager
    )
    val gardenViewModel: GardenViewModel = viewModel()

    val uiState by authViewModel.uiState.collectAsState()

    // Show spinner while session is being checked
    if (!uiState.sessionChecked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F0E8)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF4A7A2F))
        }
        return
    }

    val startDestination = if (uiState.isAuthenticated) "home" else "dashboard1"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ── Onboarding ────────────────────────────────────────────────
        composable("dashboard1") { FirstScreen(navController) }
        composable("dashboard2") { Entry_Dashboard2(navController) }
        composable("dashboard3") { Entry_Dashboard3(navController) }
        composable("dashboard4") { Entry_Dashboard4(navController) }

        // ── Auth ──────────────────────────────────────────────────────
        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // ── Home ──────────────────────────────────────────────────────
        composable("home") {
            LaunchedEffect(uiState.isAuthenticated, uiState.sessionChecked) {
                if (uiState.sessionChecked && !uiState.isAuthenticated) {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
            MainHome(
                onScanPlant        = { navController.navigate("IdentifyPlant") },
                onDiagnosePlant    = { navController.navigate("DiagnosePlant") },
                onGardenPlan       = { navController.navigate("gardenPlanner") },
                onLogout           = { activity?.let { authViewModel.logout(it) } },
                onReminderClick    = { navController.navigate("reminder") },
                onSustainableClick = { navController.navigate("sustainable") },
                onCompostReminderClick = { navController.navigate("compostReminder") },
                userEmail = uiState.userEmail,
                scanHistoryViewModel = scanHistoryViewModel,
                gardenViewModel = gardenViewModel
            )
        }

        // ── Plant Identification ───────────────────────────────────────
        composable("IdentifyPlant") {
            PlantIdentifyScreen(
                scanHistoryViewModel = scanHistoryViewModel,
                onBack = { navController.popBackStack() },
                onCareRecommendation = { plantName ->
                    navController.navigate("care/$plantName")
                }
            )
        }

        composable("care/{plantName}") { backStackEntry ->
            val plantName = backStackEntry.arguments?.getString("plantName") ?: ""
            CareScreen(
                plantName = plantName,
                onBack    = { navController.popBackStack() }
            )
        }

        // ── Disease Detection ─────────────────────────────────────────
        // ── Disease Detection ─────────────────────────────────────────
        composable("DiagnosePlant") {
            DiseaseDetectionScreen(
                scanHistoryViewModel = scanHistoryViewModel,
                onBack = { navController.popBackStack() },   // ← add this
                onAddCureClick = { diseaseName ->
                    val cleaned = diseaseName
                        .replace("_", " ")
                        .trim()
                    navController.navigate("disease_cure?query=$cleaned")
                }
            )
        }
        composable(
            route = "disease_cure?query={query}",
            arguments = listOf(navArgument("query") { defaultValue = "" })
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            DiseaseCureScreen(
                initialSearch = query,
                onBack = { navController.popBackStack() }
            )
        }

        // ── Garden Planner Flow ───────────────────────────────────────
        composable("gardenPlanner") {
            HomeScreen(
                viewModel      = gardenViewModel,
                onPhotoTaken   = { navController.navigate("gardenMarkup") },

                onBack          = { navController.popBackStack() }
            )
        }

        composable("gardenMarkup") {
            MarkupScreen(
                viewModel = gardenViewModel,
                onBack    = { navController.popBackStack() },
                onNext    = {
                    gardenViewModel.calculate()
                    navController.navigate("gardenMeasurement")
                }
            )
        }

        composable("gardenMeasurement") {
            MeasurementInputScreen(
                viewModel   = gardenViewModel,
                onBack      = { navController.popBackStack() },
                scanHistoryViewModel = scanHistoryViewModel,
                onCalculate = {
                    navController.navigate("gardenResult")   // calculate() now called inside the screen
                }
            )
        }

        composable("gardenResult") {
            ResultScreen(

                viewModel  = gardenViewModel,
                onBack     = { navController.popBackStack() },
                onSeePlants = {
                    gardenViewModel.loadPlants()
                    navController.navigate("gardenPlants")
                }
            )
        }

        composable("gardenPlants") {
            PlantsScreen(scanHistoryViewModel = scanHistoryViewModel,
                viewModel  = gardenViewModel,
                onBack     = { navController.popBackStack() },
                onPlantCare = { plantName ->
                    gardenViewModel.loadCare(plantName)
                    navController.navigate("gardenCare")
                }
            )
        }

        composable("gardenCare") {
            com.example.aranyani3.screens.garden_planner.CareScreen(
                viewModel = gardenViewModel,
                onBack    = { navController.popBackStack() }
            )
        }

        // ── Plant Reminder ────────────────────────────────────────────
        composable("reminder") {
            PlantReminderScreen(
                onBack  = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable("compostReminder") {
            CompostReminderScreen(
                onBack  = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        // ── Sustainable Growing ───────────────────────────────────────
        composable("sustainable") {
            SustainableGardenScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}