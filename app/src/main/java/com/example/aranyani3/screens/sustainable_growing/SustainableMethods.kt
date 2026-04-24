package com.example.aranyani3.screens.sustainable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GardenPractice(
    val method: String,
    val whatItMeans: String,
    val howToDoIt: String,
    val bestFor: String,
    val benefits: String
)

val indoorPlantPractices = listOf(
    GardenPractice("Composting (Kitchen Waste)", "Turning food scraps into nutrient-rich soil", "Collect veg peels, fruit waste → compost bin → use after decomposition", "All indoor & flowering plants", "Reduces waste, improves soil fertility"),
    GardenPractice("Vermicomposting", "Composting using earthworms", "Add worms (Eisenia fetida) to organic waste", "Flowering plants, nutrient-demanding plants", "Faster compost, rich in nutrients"),
    GardenPractice("Cocopeat Soil Mix", "Using coconut husk fiber instead of soil", "Mix cocopeat + compost + perlite", "Indoor plants", "Lightweight, retains moisture"),
    GardenPractice("Organic Potting Mix", "Chemical-free soil blend", "Garden soil + compost + sand/cocopeat", "All plants", "Healthy root growth, eco-friendly"),
    GardenPractice("Natural Fertilizers", "Using organic nutrients", "Banana peel water, rice water, compost tea", "Flowering plants", "Improves blooming naturally"),
    GardenPractice("Neem Oil Spray", "Natural pest control", "Mix neem oil + water + mild soap, spray weekly", "All plants", "Prevents pests without chemicals"),
    GardenPractice("Companion Planting (Indoor)", "Growing plants that support each other", "Example: Tulsi near flowering plants", "Flowering plants", "Natural pest control, better growth"),
    GardenPractice("Rainwater Harvesting", "Using collected rainwater", "Store rainwater and use for watering", "All plants", "Chemical-free water, sustainable"),
    GardenPractice("Self-Watering Pots (DIY)", "Reduce water waste", "Use bottle/wick system", "Indoor plants", "Saves water, low maintenance"),
    GardenPractice("Mulching (Organic)", "Covering soil with organic material", "Use dry leaves, coconut husk on topsoil", "Flowering plants", "Retains moisture, reduces weeds"),
    GardenPractice("Seed Saving & Propagation", "Reusing plant materials", "Collect seeds, stem cuttings", "Flowering plants, herbs", "Saves money, sustainable cycle"),
    GardenPractice("Natural Growth Boosters", "Homemade plant tonics", "Buttermilk spray, seaweed solution", "Flowering plants", "Boosts flowering and immunity"),
    GardenPractice("Upcycled Planters", "Reusing containers", "Use bottles, tins, old mugs", "Indoor plants", "Reduces plastic waste"),
    GardenPractice("Sunlight Optimization", "Efficient light use", "Place near windows, rotate plants", "Indoor plants", "Better growth without artificial light"),
    GardenPractice("Air-Purifying Plant Selection", "Choosing beneficial plants", "Snake plant, peace lily, areca palm", "Indoor plants", "Improves air quality"),
    GardenPractice("Drip Irrigation (DIY)", "Slow water delivery", "Bottle with small holes", "All plants", "Saves water, prevents overwatering"),
    GardenPractice("Organic Pest Traps", "Non-toxic pest removal", "Yellow sticky traps, sugar traps", "Flowering plants", "Safe pest control"),
    GardenPractice("Bio-Enzymes (from citrus waste)", "Natural plant cleaner/fertilizer", "Ferment citrus peels + jaggery", "Indoor plants", "Eco-friendly nutrient boost"),
    GardenPractice("Minimal Soil Disturbance", "Avoid over-repotting", "Repot only when needed", "All plants", "Maintains soil ecosystem"),
    GardenPractice("Local/Native Plant Selection", "Growing plants suited to your climate", "Choose Indian native flowering plants", "All plants", "Less maintenance, higher success rate")
)

val foodPlantPractices = listOf(
    GardenPractice("Composting (Kitchen Waste)", "Converting food waste into fertilizer", "Use veg peels, fruit scraps → compost bin", "All vegetables, fruits", "Improves soil fertility, zero waste"),
    GardenPractice("Vermicomposting", "Using earthworms to enrich compost", "Add red worms to organic waste", "Leafy greens, tomatoes, chillies", "High nutrient content, faster compost"),
    GardenPractice("Organic Soil Mix", "Chemical-free growing medium", "Mix garden soil + compost + cocopeat", "All crops", "Healthy roots, long-term soil health"),
    GardenPractice("Crop Rotation", "Changing crops each cycle", "Rotate legumes → leafy → fruit crops", "All vegetables", "Prevents soil depletion, reduces pests"),
    GardenPractice("Companion Planting", "Growing supportive plants together", "Tomato + basil, carrot + onion", "Vegetables & herbs", "Natural pest control, better yield"),
    GardenPractice("Mulching", "Covering soil surface", "Use straw, dry leaves, coco husk", "Root crops, fruit plants", "Retains moisture, reduces weeds"),
    GardenPractice("Drip Irrigation (DIY)", "Controlled water supply", "Bottle drip or pipe system", "All crops", "Saves water, efficient irrigation"),
    GardenPractice("Rainwater Harvesting", "Using collected rainwater", "Store and use for watering", "All crops", "Chemical-free, sustainable water source"),
    GardenPractice("Natural Fertilizers", "Homemade plant nutrients", "Banana peel water, compost tea, cow dung", "Fruiting plants (tomato, brinjal)", "Improves yield organically"),
    GardenPractice("Green Manure", "Growing plants to enrich soil", "Grow legumes, then mix into soil", "Soil preparation stage", "Adds nitrogen naturally"),
    GardenPractice("Biofertilizers", "Beneficial microbes for soil", "Use Rhizobium, Azotobacter", "Pulses, legumes", "Enhances nutrient absorption"),
    GardenPractice("Neem Oil / Organic Sprays", "Natural pest control", "Spray neem oil, garlic-chilli spray", "All crops", "Safe, chemical-free pest control"),
    GardenPractice("Seed Saving", "Reusing seeds from plants", "Collect and store seeds from harvest", "Tomatoes, chillies, beans", "Cost-effective, sustainable cycle"),
    GardenPractice("Raised Bed Gardening", "Growing in elevated beds", "Create soil beds above ground", "Vegetables", "Better drainage, root growth"),
    GardenPractice("Container Gardening", "Growing in pots", "Use grow bags, buckets", "Herbs, leafy greens, small veggies", "Space-efficient, urban-friendly"),
    GardenPractice("Hydroponics (Organic Nutrients)", "Soil-less growing", "Use nutrient-rich water", "Leafy greens, herbs", "Fast growth, less water usage"),
    GardenPractice("Intercropping", "Growing multiple crops together", "Example: corn + beans", "Vegetables, grains", "Maximizes space, reduces pests"),
    GardenPractice("Polyculture Farming", "Growing diverse crops", "Mix vegetables, herbs, flowers", "All crops", "Improves biodiversity"),
    GardenPractice("Minimal Tillage", "Avoid disturbing soil", "No deep digging, just loosening", "All crops", "Preserves soil microbes"),
    GardenPractice("Shade Net / Natural Shading", "Protecting from harsh sunlight", "Use cloth/net for partial shade", "Leafy greens", "Prevents stress, better growth"),
    GardenPractice("Organic Weed Control", "Removing weeds naturally", "Hand weeding, mulching", "All crops", "No chemical contamination"),
    GardenPractice("Buttermilk / Jeevamrutha", "Traditional Indian growth boosters", "Spray or add to soil", "Vegetables, fruits", "Boosts microbial activity"),
    GardenPractice("Azolla Cultivation", "Growing nitrogen-rich aquatic fern", "Add to soil or compost", "Rice, vegetables", "Natural nitrogen fertilizer"),
    GardenPractice("Terrace Gardening", "Growing on rooftops", "Use containers/grow beds", "Urban vegetables", "Uses unused space"),
    GardenPractice("Seasonal Crop Selection", "Growing crops in right season", "Follow local planting cycles", "All crops", "Higher yield, less disease")
)

val GreenDark   = Color(0xFF1B4332)
val GreenMid    = Color(0xFF2D6A4F)
val GreenLight  = Color(0xFF52B788)
val GreenPastel = Color(0xFFD8F3DC)
val Cream       = Color(0xFFF8F5EE)
val Amber       = Color(0xFFE9C46A)
val TextDark    = Color(0xFF1A1A1A)
val TextGray    = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SustainableGardenScreen(
    onBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val tabs = listOf("🌿  Indoor Plants", "🥦  Food Plants")
    val currentData = if (selectedTab == 0) indoorPlantPractices else foodPlantPractices

    val filtered = remember(searchQuery, currentData) {
        if (searchQuery.isBlank()) currentData
        else currentData.filter {
            it.method.contains(searchQuery, ignoreCase = true) ||
                    it.benefits.contains(searchQuery, ignoreCase = true) ||
                    it.bestFor.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = Cream,
        topBar = {
            Column(
                modifier = Modifier
                    .background(Brush.verticalGradient(listOf(GreenDark, GreenMid)))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Sustainable Growing",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.3.sp
                        )
                        Text(
                            text = "Organic & eco-friendly practices",
                            fontSize = 12.sp,
                            color = GreenLight
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search practices...", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GreenLight) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenLight,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = GreenLight,
                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.08f)
                    ),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = GreenDark,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 3.dp,
                        color = Amber
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index; searchQuery = "" },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenPastel)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${filtered.size} practices",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenMid
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = filtered, key = { "${selectedTab}_${it.method}" }) { practice ->
                    PracticeCard(practice = practice)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun PracticeCard(practice: GardenPractice) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(250))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(GreenPastel),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = practiceEmoji(practice.method), fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = practice.method,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = practice.whatItMeans,
                        fontSize = 12.sp,
                        color = TextGray,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = GreenMid
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(250)),
                exit = shrinkVertically(animationSpec = tween(200))
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = GreenPastel, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(label = "How To Do It", value = practice.howToDoIt)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "Best For", value = practice.bestFor)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "Benefits", value = practice.benefits)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GreenMid, letterSpacing = 0.4.sp)
        Text(
            text = value,
            fontSize = 13.sp,
            color = TextDark,
            lineHeight = 19.sp,
            modifier = Modifier
                .padding(top = 3.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(GreenPastel.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

fun practiceEmoji(method: String): String = when {
    method.contains("Compost",      ignoreCase = true) -> "♻️"
    method.contains("Vermi",        ignoreCase = true) -> "🪱"
    method.contains("Neem",         ignoreCase = true) -> "🌿"
    method.contains("Drip",         ignoreCase = true) ||
            method.contains("Irrigation",   ignoreCase = true) -> "💧"
    method.contains("Rain",         ignoreCase = true) -> "🌧️"
    method.contains("Seed",         ignoreCase = true) -> "🌾"
    method.contains("Mulch",        ignoreCase = true) -> "🍂"
    method.contains("Fertiliz",     ignoreCase = true) -> "🧪"
    method.contains("Biofertili",   ignoreCase = true) -> "🔬"
    method.contains("Soil",         ignoreCase = true) -> "🪴"
    method.contains("Companion",    ignoreCase = true) -> "🤝"
    method.contains("Pest",         ignoreCase = true) -> "🐛"
    method.contains("Sun",          ignoreCase = true) -> "☀️"
    method.contains("Air",          ignoreCase = true) -> "💨"
    method.contains("Terrace",      ignoreCase = true) -> "🏙️"
    method.contains("Hydro",        ignoreCase = true) -> "🚿"
    method.contains("Rotation",     ignoreCase = true) -> "🔄"
    method.contains("Upcycl",       ignoreCase = true) -> "♻️"
    method.contains("Native",       ignoreCase = true) ||
            method.contains("Local",        ignoreCase = true) -> "🗺️"
    method.contains("Raised",       ignoreCase = true) -> "🌻"
    method.contains("Container",    ignoreCase = true) -> "🪣"
    method.contains("Shade",        ignoreCase = true) -> "⛱️"
    method.contains("Azolla",       ignoreCase = true) -> "🌊"
    method.contains("Buttermilk",   ignoreCase = true) ||
            method.contains("Jeevan",       ignoreCase = true) -> "🥛"
    method.contains("Green Manure", ignoreCase = true) -> "🌱"
    method.contains("Intercrop",    ignoreCase = true) -> "🌽"
    method.contains("Polyculture",  ignoreCase = true) -> "🌈"
    method.contains("Tillage",      ignoreCase = true) -> "⛏️"
    method.contains("Seasonal",     ignoreCase = true) -> "📅"
    else -> "🌿"
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSustainableGardenScreen() {
    MaterialTheme { SustainableGardenScreen() }
}