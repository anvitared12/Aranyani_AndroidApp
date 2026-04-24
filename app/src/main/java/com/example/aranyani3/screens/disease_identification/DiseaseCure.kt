package com.example.aranyani3.screens.disease_identification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Data ────────────────────────────────────────────────────────────────────

data class PlantDisease(
    val name: String,
    val symptoms: String,
    val cause: String,
    val treatment: String,
    val prevention: String
)

val diseaseDataset = listOf(
    PlantDisease("Aloe Anthracnose", "Dark sunken spots", "Fungus", "Remove infected leaves, apply copper fungicide", "Avoid wet leaves, good airflow"),
    PlantDisease("Aloe LeafSpot", "Brown/black spots", "Fungus/Bacteria", "Neem oil spray, prune leaves", "Avoid overwatering"),
    PlantDisease("Aloe Rust", "Reddish pustules", "Fungus", "Fungicide, remove infected parts", "Keep leaves dry"),
    PlantDisease("Aloe Sunburn", "Bleached/brown patches", "Excess sun", "Move to shade", "Gradual sunlight exposure"),
    PlantDisease("Blight (Hibiscus)", "Yellowing, black spots", "Fungus", "Fungicide, remove leaves", "Avoid humidity"),
    PlantDisease("Blight (Rose)", "Leaf spots, wilting", "Fungus", "Prune, fungicide", "Air circulation"),
    PlantDisease("Cactus Dactylopius (Mealybug)", "White cotton patches", "Insects", "Alcohol spray, neem oil", "Inspect regularly"),
    PlantDisease("Death Leaf (Dwarf White Bauhinia)", "Dry falling leaves", "Root rot/disease", "Check roots, repot, fungicide", "Avoid overwatering"),
    PlantDisease("Death Leaf (Hibiscus)", "Sudden drying", "Infection", "Remove affected parts", "Maintain soil health"),
    PlantDisease("Early Blight (Night Jasmine)", "Ring spots, yellowing", "Fungus", "Fungicide spray", "Avoid leaf wetness"),
    PlantDisease("Insect Bite (Crape Jasmine)", "Holes in leaves", "Pests", "Neem oil/insecticide", "Regular checks"),
    PlantDisease("Money Plant Bacterial Wilt", "Sudden wilting", "Bacteria", "Remove plant, disinfect soil", "Use clean tools"),
    PlantDisease("Money Plant Manganese Toxicity", "Yellow veins", "Nutrient excess", "Flush soil, repot", "Balanced fertilizer"),
    PlantDisease("Red Spot (Night Jasmine)", "Red/brown spots", "Fungus", "Fungicide, prune", "Airflow"),
    PlantDisease("Scorch (Banana Bush)", "Burnt edges", "Heat/drought", "Increase watering, shade", "Avoid harsh sun"),
    PlantDisease("Scorch (Hibiscus)", "Dry edges", "Heat stress", "Water properly, shade", "Maintain moisture"),
    PlantDisease("Snake Plant Anthracnose", "Dark lesions", "Fungus", "Remove leaves, fungicide", "Avoid overwatering"),
    PlantDisease("Snake Plant Leaf Withering", "Soft droopy leaves", "Overwatering", "Stop watering, repot", "Well-drained soil"),
    PlantDisease("Spider Plant Fungal Leaf Spot", "Spots on leaves", "Fungus", "Neem oil, remove leaves", "Avoid wet leaves"),
    PlantDisease("Spider Plant Leaf Tip Necrosis", "Brown tips", "Salt buildup", "Use filtered water, trim tips", "Avoid chemicals"),
    // Other diseases
    PlantDisease("Overwatering", "Yellow leaves, mushy roots, foul smell", "Too much water, poor drainage", "Stop watering, repot, remove rotten roots", "Water only when soil is dry"),
    PlantDisease("Underwatering", "Dry, crispy leaves, drooping", "Lack of water", "Deep watering, soak soil properly", "Maintain regular watering schedule"),
    PlantDisease("Root Rot", "Black roots, plant dying", "Fungal growth due to waterlogging", "Trim roots, repot in dry soil", "Well-draining soil"),
    PlantDisease("Nutrient Deficiency", "Yellowing leaves, slow growth", "Lack of nutrients", "Add balanced fertilizer", "Regular feeding"),
    PlantDisease("Overfertilization", "Leaf burn, salt crust on soil", "Excess fertilizer", "Flush soil with water", "Use fertilizer in moderation"),
    PlantDisease("Sunburn", "Brown/white patches", "Excess sunlight", "Move to indirect light", "Gradual sun exposure"),
    PlantDisease("Low Light Stress", "Leggy growth, small leaves", "Not enough sunlight", "Move to brighter spot", "Provide adequate light"),
    PlantDisease("Pest Infestation (Aphids)", "Sticky leaves, tiny insects", "Sap-sucking pests", "Neem oil spray, soap water", "Regular inspection"),
    PlantDisease("Mealybugs", "White cotton-like clusters", "Pest infestation", "Alcohol wipe, neem oil", "Clean leaves regularly"),
    PlantDisease("Spider Mites", "Tiny webs, yellow spots", "Dry conditions", "Increase humidity, neem oil", "Mist plant"),
    PlantDisease("Fungal Infection", "Spots, mold on leaves", "High humidity", "Fungicide, remove leaves", "Good airflow"),
    PlantDisease("Bacterial Infection", "Soft rot, foul smell", "Bacteria in water/soil", "Remove affected parts", "Sterilize tools"),
    PlantDisease("Leaf Curling", "Leaves twisting", "Water stress, pests", "Fix watering, treat pests", "Stable care routine"),
    PlantDisease("Leaf Drop", "Sudden leaf fall", "Stress (temp/water change)", "Stabilize environment", "Avoid sudden changes"),
    PlantDisease("Transplant Shock", "Wilting after repotting", "Root disturbance", "Keep in shade, water lightly", "Gentle repotting"),
    PlantDisease("Poor Drainage", "Waterlogged soil", "Compact soil", "Add sand/perlite", "Use proper soil mix"),
    PlantDisease("Humidity Stress", "Brown edges", "Dry air", "Mist plant, use humidifier", "Maintain humidity"),
    PlantDisease("Cold Damage", "Blackened leaves", "Low temperature", "Move indoors", "Avoid cold exposure"),
    PlantDisease("Heat Stress", "Wilting, dry leaves", "High temperature", "Provide shade, water", "Avoid midday sun"),
    PlantDisease("Weed Competition", "Slow growth", "Nutrients taken by weeds", "Remove weeds", "Clean soil regularly"),
)

// ─── Colors ───────────────────────────────────────────────────────────────────

val DarkGreen = Color(0xFF1B4332)
val MediumGreen = Color(0xFF2D6A4F)
val LightGreen = Color(0xFF52B788)
val PaleGreen = Color(0xFFD8F3DC)
val AccentGreen = Color(0xFF74C69D)
val BackgroundColor = Color(0xFFF4F1EE)
val CardColor = Color.White
val TextPrimary = Color(0xFF1B1B1B)
val TextSecondary = Color(0xFF6B6B6B)

// ─── Main Screen ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseCureScreen(
    initialSearch: String = "",
    onBack: () -> Unit = {}
) {
    val cleaned = initialSearch.replace("_", " ").trim()
    var searchQuery by remember { mutableStateOf(cleaned) }

    val filtered = remember(searchQuery,) {
        if (searchQuery.isBlank()) diseaseDataset
        else diseaseDataset.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.symptoms.contains(searchQuery, ignoreCase = true) ||
                    it.cause.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGreen)
                .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBack() }   // ← was: { /* navigate back */ }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Plant Disease Guide",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Identify symptoms, causes & cures",
                            color = AccentGreen,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search diseases, symptoms...", color = TextSecondary, fontSize = 14.sp)
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.clickable { searchQuery = "" }
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ── Count badge ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = PaleGreen
            ) {
                Text(
                    text = "${filtered.size} diseases",
                    color = MediumGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        // ── List ─────────────────────────────────────────────────────────────
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filtered, key = { it.name }) { disease ->
                DiseaseCard(disease = disease)
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ─── Disease Card ─────────────────────────────────────────────────────────────

@Composable
fun DiseaseCard(disease: PlantDisease) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Row: icon + name + chevron ────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Leaf icon circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(PaleGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = MediumGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = disease.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = disease.symptoms,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MediumGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            // ── Expanded detail ───────────────────────────────────────────
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = PaleGreen, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        icon = Icons.Default.BugReport,
                        label = "Cause",
                        value = disease.cause,
                        iconTint = Color(0xFFE07B39)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        icon = Icons.Default.Healing,
                        label = "Treatment",
                        value = disease.treatment,
                        iconTint = Color(0xFF2D6A4F)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        icon = Icons.Default.Shield,
                        label = "Prevention",
                        value = disease.prevention,
                        iconTint = Color(0xFF1565C0)
                    )
                }
            }
        }
    }
}

// ─── Detail Row ───────────────────────────────────────────────────────────────

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = iconTint.copy(alpha = 0.12f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 18.sp
            )
        }
    }
}

// ─── Bottom Nav (optional shell) ──────────────────────────────────────────────

@Composable
fun DiseaseCureApp() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = MediumGreen
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkGreen,
                        selectedTextColor = DarkGreen,
                        indicatorColor = PaleGreen
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "My Plants") },
                    label = { Text("My Plants") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkGreen,
                        selectedTextColor = DarkGreen,
                        indicatorColor = PaleGreen
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Cloud, contentDescription = "Weather") },
                    label = { Text("Weather") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkGreen,
                        selectedTextColor = DarkGreen,
                        indicatorColor = PaleGreen
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Refresh, contentDescription = "Cure") },
                    label = { Text("Cure") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = DarkGreen,
                        indicatorColor = DarkGreen
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            DiseaseCureScreen()
        }
    }
}