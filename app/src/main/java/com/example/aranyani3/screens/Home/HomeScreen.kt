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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.aranyani3.auth.AuthViewModel
import com.example.aranyani3.screens.myplants.MyScans
import com.example.aranyani3.screens.weather.WeatherScreen
import com.example.sustainablegrowing.Regrow
import com.example.aranyani3.auth.Auth0Manager
import com.example.aranyani3.screens.myplants.MyScans
import com.example.aranyani3.viewmodel.ScanHistoryViewModel


// ─────────────────────────────────────────────
//  Color Tokens
// ─────────────────────────────────────────────
private object AppColors {
    val DeepForest       = Color(0xFF2D4A1E)
    val RichFern         = Color(0xFF4A7A2F)
    val Garden           = Color(0xFF6B9E45)
    val Leaf             = Color(0xFF8FBF5E)
    val Sage             = Color(0xFFB5D48A)
    val MintMist         = Color(0xFFD8EAC0)
    val LinenLeaf        = Color(0xFFEBF0D6)

    val MossDark         = Color(0xFF3B4A22)
    val Olive            = Color(0xFF5C6E30)
    val Herb             = Color(0xFF879A4E)

    val Cream            = Color(0xFFF5F0E8)
    val Parchment        = Color(0xFFEDE6D8)
    val Sand             = Color(0xFFE2D9C6)
    val Latte            = Color(0xFFC8BC9E)

    val Terracotta       = Color(0xFFC96B3A)
    val Harvest          = Color(0xFFE8893E)
    val Golden           = Color(0xFFF2B135)
    val Sunrise          = Color(0xFFF7D06A)

    val Headline         = Color(0xFF1A2810)
    val BodyDark         = Color(0xFF2F4A1C)
    val Body             = Color(0xFF4D6E35)
    val Muted            = Color(0xFF7A9E5C)

    val heroGradient = Brush.verticalGradient(
        0f to Color(0x00000000),
        0.5f to Color(0x44000000),
        1f to Color(0xBB1A2810)
    )
    val cardGradient = Brush.linearGradient(
        listOf(Color(0xFF3B5A28), Color(0xFF6B9E45))
    )
    val reminderGradient = Brush.linearGradient(
        listOf(Color(0xFF4A7A2F), Color(0xFF6B9E45), Color(0xFF879A4E))
    )
    val exploreGradient = Brush.linearGradient(
        listOf(Color(0xFF2D4A1E), Color(0xFF3B5A28))
    )
    val navGradient = Brush.linearGradient(
        listOf(Color(0xFFD8EAC0), Color(0xFFEBF0D6))
    )
    val appBackground = Brush.verticalGradient(
        listOf(Color(0xFFF5F0E8), Color(0xFFEBF0D6))
    )
}

// ─────────────────────────────────────────────
//  Root Screen
// ─────────────────────────────────────────────
enum class Screen { Home, MyPlants, Weather, Regrow }

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
    scanHistoryViewModel: ScanHistoryViewModel
) {
    // 1. Add state to track the current screen
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.appBackground)
    ) {
        // 2. Use a when statement to display the correct screen content
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
                MyScans(viewModel = scanHistoryViewModel)
            }
            Screen.Weather -> {
                // Placeholder for Weather screen
                WeatherScreen()
            }

            Screen.Regrow -> {
                Regrow(onBack = { currentScreen = Screen.Home })
            }
        }

        // 3. Update the BottomNavBar callbacks to change the currentScreen state
        BottomNavBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            onHome = { currentScreen = Screen.Home },
            onMyPlants = { currentScreen = Screen.MyPlants },
            onWeather = { currentScreen = Screen.Weather },
            onRegrow = { currentScreen = Screen.Regrow },
        )
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
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp)
    ) {
        item { TopBar(onLogout = onLogout) }
        item { Spacer(modifier = Modifier.height(10.dp)) }
        item { HeaderSection(onLogout = onLogout, userEmail = userEmail) }
        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            ActionButtons(
                onScanPlant = onScanPlant,
                onDiagnosePlant = onDiagnosePlant,
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        item { GardenPlanCard(onClick = onGardenPlan) }
        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            CareReminderSection(
                onReminderClick = onReminderClick,
                onCompostReminderClick = onCompostReminderClick   // add this
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
        item { ExploreCard(onClick = onSustainableClick) }
        // ✅ FIX 4: onClick passed to ExploreCard
    }
}

// ─────────────────────────────────────────────
//  Top Bar with Logout
// ─────────────────────────────────────────────
@Composable
fun TopBar(onLogout: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("🌿", fontSize = 20.sp)
            Text(
                text = "Aranyani",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.DeepForest,
                letterSpacing = (-0.4).sp
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Header (Hero card)
// ─────────────────────────────────────────────
@Composable
fun HeaderSection(
    onLogout: () -> Unit = {},
    userEmail: String? = null) {
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.heroGradient)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(14.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.18f))
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(50)
                )
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
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Logout",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            val displayName = userEmail
                ?.substringBefore("@")
                ?.replaceFirstChar { it.uppercase() }
                ?: "Gardener"
            Text(
                text = "Hello, $displayName 🌿",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Your garden is thriving today",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}


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
            .height(130.dp)
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(brush = Brush.linearGradient(colors = cardColors))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }

            Column {
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.2).sp
                )
                if (subtitleText.isNotEmpty()) {
                    Text(
                        text = subtitleText,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
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
            .height(80.dp)
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFFE8893E), Color(0xFFF2B135))
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        listOf(Color.White.copy(0.08f), Color.Transparent)
                    )
                )
        )

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
                Text("🍊", fontSize = 28.sp)
                Column {
                    Text(
                        "Plan your Garden",
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        "Seasonal planting guide →",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("→", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
    onCompostReminderClick: () -> Unit = {},   // add this
    onSeeAllClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val reminders = listOf(
        ReminderItem("Composting Schedule", "🍂", setOf(1, 3, 5)),
        ReminderItem("Watering Schedule", "💧", setOf(2, 4, 6, 7))
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(2.dp))

        reminders.forEachIndexed { index, reminder ->
            ReminderCard(
                title = reminder.title,
                emoji = reminder.emoji,
                activeDays = reminder.activeDays,
                // first card = composting, second = watering
                onClick = if (index == 0) onCompostReminderClick else onReminderClick
            )
        }
    }
}

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
            .clip(RoundedCornerShape(22.dp))
            .background(AppColors.reminderGradient)
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(emoji, fontSize = 16.sp)
                Text(
                    title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}



// ─────────────────────────────────────────────
//  Explore Card  ✅ FIX 5: onClick parameter added and wired up
// ─────────────────────────────────────────────
@Composable
fun ExploreCard(onClick: () -> Unit = {}) {       // ✅ FIX: onClick parameter added
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }               // ✅ FIX: clickable wired to onClick
            .background(AppColors.exploreGradient)
            .border(
                1.dp,
                AppColors.Garden.copy(alpha = 0.4f),
                RoundedCornerShape(24.dp)
            )
            .padding(22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = AppColors.Garden.copy(alpha = 0.3f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        "  ECO GUIDE  ",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.MintMist,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
                Text(
                    "Explore Sustainable\n& Organic Growing",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Natural methods for a healthier garden →",
                    color = AppColors.Sage,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(AppColors.Garden.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .border(
                        1.dp,
                        AppColors.Garden.copy(alpha = 0.4f),
                        RoundedCornerShape(16.dp)
                    ),
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
    onHome: () -> Unit = {},
    onMyPlants: () -> Unit = {},
    onWeather: () -> Unit = {},
    onRegrow: () -> Unit = {},
) {
    var selectedRoute by remember { mutableStateOf(NavRoutes.Home) }

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(AppColors.navGradient)
            .border(
                1.dp,
                AppColors.Sage.copy(alpha = 0.5f),
                RoundedCornerShape(28.dp)
            )
            .fillMaxWidth()
            .height(68.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
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
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(AppColors.RichFern)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text,

                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.Body,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text,
                color = AppColors.Muted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}