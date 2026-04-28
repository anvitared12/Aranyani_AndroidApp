package com.example.aranyani2.ui_screens.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.aranyani3.R
import com.example.aranyani3.auth.AuthViewModel
import com.example.aranyani3.screens.myplants.MyScans
import com.example.aranyani3.screens.weather.WeatherScreen
import com.example.sustainablegrowing.Regrow
import com.example.aranyani3.auth.Auth0Manager
import com.example.aranyani3.viewmodel.GardenViewModel
import com.example.aranyani3.viewmodel.ScanHistoryViewModel
import com.example.aranyani3.screens.garden_planner.PlantsScreen

val MyCustomFont = FontFamily(
    Font(R.font.font)
)

private object AppColors {
    val Leaf             = Color(0xFF8FBF5E)
    val Harvest          = Color(0xFFE8893E)
    val AppBg            = Color(0xFFF2F2F2)
    val CardSurface      = Color(0xFFEEEDED)
    val CardSurfaceAlt   = Color(0xFFEEEDED)
    val CardTitle        = Color(0xFF1A1A1A)
    val CardSubtitle     = Color(0xFF888888)
    val CardStat         = Color(0xFF555555)
    val SearchBg         = Color(0xFFFFFFFF)
    val NavBg            = Color(0xFFFDFDFD)
    val NavBorder        = Color(0xFFDDDDDD)
    val GreenBtn         = Color(0xFF7CB342)
    val LogoGreen        = Color(0xFF3DAF3F)
    val heroGradient = Brush.verticalGradient(
        0f to Color(0x00000000),
        0.5f to Color(0x44000000),
        1f to Color(0xBB1A2810)
    )
}

// ── Add Plants to the Screen enum ──
enum class Screen { Home, MyPlants, Weather, Regrow, Plants }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainHome(
    onScanPlant: () -> Unit = {},
    onDiagnosePlant: () -> Unit = {},
    onGardenPlan: () -> Unit = {},
    onLogout: () -> Unit = {},
    onReminderClick: () -> Unit = {},
    onSustainableClick: () -> Unit = {},
    onCompostReminderClick: () -> Unit,
    userEmail: String? = null,
    scanHistoryViewModel: ScanHistoryViewModel,
    gardenViewModel: GardenViewModel          // ← add this
) {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.homebackground),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        when (currentScreen) {
            Screen.Home -> {
                HomeDashboardBody(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopStart),
                    onScanPlant = onScanPlant,
                    onDiagnosePlant = onDiagnosePlant,
                    onGardenPlan = onGardenPlan,
                    onLogout = onLogout,
                    onReminderClick = onReminderClick,
                    onSustainableClick = onSustainableClick,
                    onCompostReminderClick = onCompostReminderClick,
                    userEmail = userEmail
                )
            }

            Screen.MyPlants -> {
                MyScans(
                    viewModel = scanHistoryViewModel,
                    onViewPlants = { currentScreen = Screen.Plants }  // ← fixed
                )
            }

            Screen.Plants -> {
                PlantsScreen(
                    viewModel = gardenViewModel,
                    scanHistoryViewModel = scanHistoryViewModel,
                    onBack = { currentScreen = Screen.MyPlants },
                    onPlantCare = { /* navigate to care screen if you have one */ }
                )
            }

            Screen.Weather -> {
                WeatherScreen()
            }

            Screen.Regrow -> {
                Regrow(onBack = { currentScreen = Screen.Home })
            }
        }

        // Hide bottom nav when on Plants screen (it has its own TopAppBar back button)
        if (currentScreen != Screen.Plants) {
            BottomNavBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                currentScreen = currentScreen,
                onHome = { currentScreen = Screen.Home },
                onMyPlants = { currentScreen = Screen.MyPlants },
                onWeather = { currentScreen = Screen.Weather },
                onRegrow = { currentScreen = Screen.Regrow },
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Dashboard Body
// ─────────────────────────────────────────────
@Composable
fun HomeDashboardBody(
    modifier: Modifier = Modifier,
    onScanPlant: () -> Unit = {},
    onDiagnosePlant: () -> Unit = {},
    onGardenPlan: () -> Unit = {},
    onLogout: () -> Unit = {},
    onReminderClick: () -> Unit = {},
    onSustainableClick: () -> Unit = {},
    onCompostReminderClick: () -> Unit = {},
    userEmail: String? = null
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 36.dp, bottom = 120.dp)
    ) {
        item { TopBar(onLogout = onLogout) }
        item { Spacer(modifier = Modifier.height(20.dp)) }
        item {
            Column {
                Text(
                    text = "Hello,",
                    fontSize = 38.sp,
                    fontFamily = MyCustomFont,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.CardTitle,
                    letterSpacing = (-1).sp,
                    lineHeight = 42.sp
                )
                Text(
                    text = "Plant Lover ",
                    fontFamily = MyCustomFont,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-1).sp,
                    lineHeight = 46.sp
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            ActionButtons(
                onScanPlant = onScanPlant,
                onDiagnosePlant = onDiagnosePlant,
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { GardenPlanCard(onClick = onGardenPlan) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            CareReminderSection(
                onReminderClick = onReminderClick,
                onCompostReminderClick = onCompostReminderClick
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { ExploreCard(onClick = onSustainableClick) }
    }
}

// ─────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────
@Composable
fun TopBar(onLogout: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, AppColors.LogoGreen, CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("🌿", fontSize = 18.sp)
            }
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                    Text(
                        text = "Aranyani",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.CardTitle,
                        letterSpacing = (-0.3).sp
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF9CCC65))
                .clickable(onClick = onLogout)
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = AppColors.CardStat,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Logout",
                    color = AppColors.CardStat,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Header (kept for compatibility)
// ─────────────────────────────────────────────
@Composable
fun HeaderSection(
    onLogout: () -> Unit = {},
    userEmail: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(28.dp))
            .border(
                width = 1.dp,
                color = AppColors.Leaf.copy(alpha = 0.3f),
                shape = RoundedCornerShape(28.dp)
            )
    ) {
        Image(
            painter = painterResource(id = com.example.aranyani3.R.drawable.homepic),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(AppColors.heroGradient))
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)
        ) {
            val displayName = userEmail
                ?.substringBefore("@")
                ?.replaceFirstChar { it.uppercase() }
                ?: "Gardener"
            Text(
                text = "Hello, $displayName",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Your garden is thriving today",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Action Buttons
// ─────────────────────────────────────────────
@Composable
fun ActionButtons(
    onScanPlant: () -> Unit,
    onDiagnosePlant: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ActionCard(
            text = "Scan a plant",
            icon = Icons.Default.Search,
            emoji = "🔍",
            subtitleText = "Identify species",
            cardColors = listOf(Color(0xFF3B5A28), Color(0xFF6B9E45)),
            modifier = Modifier.weight(1f),
            onClick = { onScanPlant() },
        )
        ActionCard(
            text = "Diagnose",
            icon = Icons.Default.Favorite,
            emoji = "🩺",
            subtitleText = "Health check",
            cardColors = listOf(Color(0xFF4A5E1C), Color(0xFF879A4E)),
            modifier = Modifier.weight(1f),
            onClick = { onDiagnosePlant() },
        )
    }
}

// ─────────────────────────────────────────────
//  Action Card
// ─────────────────────────────────────────────
@Composable
fun ActionCard(
    text: String,
    icon: ImageVector,
    emoji: String = "",
    subtitleText: String = "",
    cardColors: List<Color> = listOf(Color(0xFF3B5A28), Color(0xFF6B9E45)),
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(120),
        label = "scale"
    )

    Box(
        modifier = modifier
            .height(160.dp)
            .scale(scale)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(AppColors.CardSurface)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Text(
            text = emoji,
            fontSize = 52.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 12.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = text,
                color = AppColors.CardTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp
            )
            if (subtitleText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subtitleText,
                    color = AppColors.CardSubtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(AppColors.GreenBtn.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Tap →",
                    color = AppColors.GreenBtn,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

data class ReminderItem(
    val title: String,
    val emoji: String,
    val activeDays: Set<Int>
)

// ─────────────────────────────────────────────
//  Garden Plan Card
// ─────────────────────────────────────────────
@Composable
fun GardenPlanCard(onClick: () -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .scale(scale)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(AppColors.CardSurface)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("🍊", fontSize = 36.sp)
                Column {
                    Text(
                        "Plan your Garden",
                        fontSize = 15.sp,
                        color = AppColors.CardTitle,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        "Seasonal planting guide",
                        fontSize = 11.sp,
                        color = AppColors.CardSubtitle
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(AppColors.Harvest.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "→",
                    color = AppColors.Harvest,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Care Reminder Section
// ─────────────────────────────────────────────
@Composable
fun CareReminderSection(
    onReminderClick: () -> Unit,
    onCompostReminderClick: () -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val reminders = listOf(
        ReminderItem("Composting Schedule", "🍂", setOf(1, 3, 5)),
        ReminderItem("Watering Schedule", "\uD83C\uDF27\uFE0F", setOf(2, 4, 6, 7))
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        reminders.forEachIndexed { index, reminder ->
            ReminderCard(
                title = reminder.title,
                emoji = reminder.emoji,
                activeDays = reminder.activeDays,
                onClick = if (index == 0) onCompostReminderClick else onReminderClick
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Reminder Card
// ─────────────────────────────────────────────
@Composable
fun ReminderCard(
    title: String,
    emoji: String,
    activeDays: Set<Int>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(AppColors.CardSurfaceAlt)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = emoji,
            fontSize = 64.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 24.dp)
        )
        Text(
            text = title,
            color = AppColors.CardTitle,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
            modifier = Modifier.padding(start = 22.dp)
        )
    }
}

// ─────────────────────────────────────────────
//  Explore Card
// ─────────────────────────────────────────────
@Composable
fun ExploreCard(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(26.dp))
            .clip(RoundedCornerShape(26.dp))
            .background(AppColors.CardSurface)
            .clickable { onClick() }
            .padding(22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AppColors.GreenBtn.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "ECO GUIDE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.GreenBtn,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Explore Sustainable\n& Organic Growing",
                    color = AppColors.CardTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Natural methods for a healthier garden →",
                    color = AppColors.CardSubtitle,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.GreenBtn.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🌱", fontSize = 32.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Bottom Nav Bar
// ─────────────────────────────────────────────
enum class NavRoutes { Home, MyPlants, Weather, Regrow }

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    currentScreen: Screen = Screen.Home,
    onHome: () -> Unit = {},
    onMyPlants: () -> Unit = {},
    onWeather: () -> Unit = {},
    onRegrow: () -> Unit = {},
) {
    var selectedRoute by remember { mutableStateOf(NavRoutes.Home) }

    // Keep selectedRoute in sync when navigating back from Plants
    selectedRoute = when (currentScreen) {
        Screen.Home -> NavRoutes.Home
        Screen.MyPlants, Screen.Plants -> NavRoutes.MyPlants
        Screen.Weather -> NavRoutes.Weather
        Screen.Regrow -> NavRoutes.Regrow
    }

    Box(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(AppColors.NavBg)
            .fillMaxWidth()
            .height(82.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            NavItem(
                text = "Home",
                icon = Icons.Default.Home,
                isSelected = selectedRoute == NavRoutes.Home,
                onClick = { selectedRoute = NavRoutes.Home; onHome() }
            )
            NavItem(
                text = "My Scans",
                icon = Icons.Default.Favorite,
                isSelected = selectedRoute == NavRoutes.MyPlants,
                onClick = { selectedRoute = NavRoutes.MyPlants; onMyPlants() }
            )
            NavItem(
                text = "Weather",
                icon = Icons.Default.Cloud,
                isSelected = selectedRoute == NavRoutes.Weather,
                onClick = { selectedRoute = NavRoutes.Weather; onWeather() }
            )
            NavItem(
                text = "Regrow",
                icon = Icons.Default.Refresh,
                isSelected = selectedRoute == NavRoutes.Regrow,
                onClick = { selectedRoute = NavRoutes.Regrow; onRegrow() }
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Nav Item
// ─────────────────────────────────────────────
@Composable
fun NavItem(
    text: String,
    icon: ImageVector,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .height(56.dp)
            .padding(horizontal = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(AppColors.GreenBtn),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFFAAAAAA),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text,
                color = Color(0xFFAAAAAA),
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}