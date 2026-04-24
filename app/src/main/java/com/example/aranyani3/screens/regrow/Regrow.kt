package com.example.sustainablegrowing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Data Model ───────────────────────────────────────────────────────────────

data class PlantInfo(
    val name: String,
    val hindi: String,
    val kannada: String,
    val telugu: String,
    val propagation: String,
    val sunlight: String,
    val soil: String,
    val harvest: String,
    val season: String,
    val container: String,
    val instruction: String,
    val iconType: PlantIconType
)

enum class PlantIconType { ROOT, LEAF, HERB, BULB, FRUIT }

// ─── Sample Data ──────────────────────────────────────────────────────────────

val foodPlants = listOf(
    PlantInfo("Beetroot", "चुकंदर", "ಚಕೊಂಡರ್", "—", "Root cuttings",
        "Full sun (6–8h)", "Loamy, well-drained; pH 6.0–7.0", "~2–3 weeks (greens)",
        "Cool season (winter/spring)", "15–20 cm pot",
        "Cut the beet's crown (top ~3 cm) and place the flat cut end in shallow water or moist soil. New green leaves sprout in 2–3 weeks. Once roots form, replant for continued growth.",
        PlantIconType.ROOT),
    PlantInfo("Carrot", "गाजर", "ಗಜರು", "గాజ్జరి", "Root cuttings",
        "Full sun (6–8h)", "Sandy loam, pH 6.0–6.8", "~3–4 weeks (greens)",
        "Cool season (winter/spring)", "10–15 cm pot",
        "Cut off the top ~2–3 cm of a fresh carrot with some green stalk. Place in ~1 cm of water in a sunny spot. New leaves emerge in days. Transplant into soil after 2–3 weeks.",
        PlantIconType.ROOT),
    PlantInfo("Celery", "अजमोदा", "—", "—", "Base cuttings",
        "Bright indirect (4–6h)", "Rich moisture-retentive loam; pH 6.5–7.0", "~3–4 weeks (leaves)",
        "Year-round", "15–20 cm pot",
        "Cut stalks 5 cm above the base. Place the base in ~2 cm water in bright indirect light. New leaves sprout from the center in 1–3 days. Transplant into potting mix after roots develop.",
        PlantIconType.LEAF),
    PlantInfo("Garlic", "लहसुन", "ಬೆಳ್ಳುಳ್ಳಿ", "వెల్లుల్లి", "Seed/Cloves",
        "Full sun (6–8h)", "Well-drained fertile loam; pH 6.0–7.0", "~120–150 days",
        "Oct–Dec sow, summer harvest", "20 cm deep pot",
        "Separate a garlic bulb into cloves. Plant each pointy end up, ~5 cm deep, 10 cm apart in full sun. Keep soil moist. Shoots appear in 2–4 weeks. Harvest when foliage yellows.",
        PlantIconType.BULB),
    PlantInfo("Ginger", "अदरक", "ಶುಂಠಿ", "అల్లము", "Rhizome cuttings",
        "Partial sun (4–6h)", "Rich loamy, well-drained; pH 5.5–6.5", "~240–300 days",
        "Apr–Jun planting", "30 cm deep pot",
        "Use a fresh ginger rhizome with visible eyes/buds. Plant ~5 cm deep in moist warm soil. Keep the pot warm in partial shade. Shoots emerge in 3–4 weeks. Harvest mature rhizomes after 8–10 months.",
        PlantIconType.ROOT),
    PlantInfo("Green Onion", "हरा प्याज़", "ಈರುಳ್ಳಿ", "ఈరేళ్ళి", "Root regrowth",
        "Full sun (6–8h)", "Light potting soil, pH 6.0–7.0", "~2–3 weeks (greens)",
        "Year-round", "10–15 cm pot",
        "Take kitchen scrap roots leaving ~2 cm white base with roots. Place root-down in a jar of water on a sunny windowsill. New shoots appear in 1–2 weeks. Replant in soil for continued harvest.",
        PlantIconType.LEAF),
    PlantInfo("Lettuce (Romaine)", "सलाद पत्ता", "ಲೆಟ್ಯುಸ್", "లెట్యూసు", "Base regrowth",
        "Full sun (6–8h)", "Rich loam, pH 6.0–7.0", "~1–2 weeks (leaves)",
        "Oct–Feb (cool season)", "15–20 cm pot",
        "Cut leaves about 2 cm above the base of a romaine head. Place base in shallow water in bright light. New leaves regrow in 5–7 days. Change water frequently. Transplant to soil once roots develop.",
        PlantIconType.LEAF),
    PlantInfo("Potato", "आलू", "ಆಲೂ ಗಡ್ಡೆ", "బంగాళాదుంప", "Tuber pieces (eyes)",
        "Full sun (6–8h)", "Loose, well-drained loam; pH 5.5–6.5", "~90–120 days",
        "Oct–Nov planting", "20–30 cm deep pot",
        "Use sprouted potato pieces with ≥1 eye. Plant 5–10 cm deep, eyes up, in loose soil. Hill soil around stems as they grow. Keep soil evenly moist. Harvest after 90–120 days when foliage dies back.",
        PlantIconType.ROOT),
    PlantInfo("Radish", "मूली", "ಮುಲ್ಲಂಗಿ", "మూలంగి", "Root regrowth",
        "Full sun (6–8h)", "Light loamy, pH 6.0–7.0", "~2–3 weeks (greens)",
        "Oct–Dec (cool season)", "10 cm pot",
        "Cut off the top ~2 cm of a radish leaving part of the root. Place cut end in water with roots submerged. New leafy tops regrow in 1–2 weeks. Change water often or plant stub in moist soil.",
        PlantIconType.ROOT),
    PlantInfo("Sweet Potato", "शकरकंद", "ಗಣಸು", "చిలకదాదుంప", "Tuber/slips (vine)",
        "Full sun (6–8h)", "Light sandy loam; pH 5.5–6.5", "~120–150 days",
        "Feb–Mar planting", "30–45 cm spacing, large pot",
        "Place a sweet potato half-submerged in water or soil. After 2–4 weeks, green slips appear. Snap off a 15–20 cm slip and plant 5–8 cm deep in warm soil. Tubers develop in 4–5 months.",
        PlantIconType.ROOT),
    PlantInfo("Turmeric", "हल्दी", "ಹಳದಿ", "పసుపు", "Rhizome",
        "Partial sun (4–6h)", "Rich loamy; pH 6.0–6.5", "~240–270 days",
        "Apr–May planting", "20–25 cm pot",
        "Use a fresh turmeric rhizome with buds. Plant ~5 cm deep. Shoots sprout in 5–10 days. Keep soil moist and semi-shaded. Harvest mature rhizomes after 8–9 months.",
        PlantIconType.ROOT),
    PlantInfo("Coriander", "धनिया", "ಕೊತ್ತಂಬರಿ", "కొత్తిమీర", "Seeds (soil)",
        "Full sun (6–8h)", "Loamy; pH 6.0–7.0", "~4–5 weeks",
        "Sept–Dec (cool Rabi)", "15–20 cm pot",
        "Sow coriander seeds 1 cm deep in moist soil. Keep consistently moist. Germination in 7–10 days. Thin to ~10 cm spacing. Leaves (cilantro) are ready to harvest 4–5 weeks after sowing.",
        PlantIconType.HERB),
    PlantInfo("Fenugreek", "मेथी", "ಮೇಂತೆ", "—", "Seeds (soil)",
        "Full sun (6–8h)", "Loamy; pH 6.0–7.0", "~3–4 weeks",
        "Oct–Dec (cool season)", "10–15 cm pot",
        "Soak fenugreek seeds overnight. Sow 1 cm deep in soil. Keep moist. Sprouts appear in 2–4 days. Harvest young leaves in 3–4 weeks by cutting just above soil — they will regrow new shoots.",
        PlantIconType.HERB),
    PlantInfo("Basil (Holy Tulsi)", "तुलसी", "ತುಳಸಿ", "—", "Seeds or Cuttings",
        "Full sun (6–8h)", "Rich loamy; pH 6.0–7.0", "~6–8 weeks",
        "Feb–Apr and Aug–Nov", "15–20 cm pot",
        "Sow seeds or plant 5–10 cm stem cuttings in potting mix. Place cuttings in water to root first, then plant. Seeds germinate in 1–2 weeks. Harvest fresh leaves after 6–8 weeks.",
        PlantIconType.HERB),
    PlantInfo("Chili / Red Pepper", "मिर्च", "ಮೆಣಸಿನಕಾಯಿ", "మిరప", "Seeds",
        "Full sun (6–8h)", "Light loam; pH 6.0–6.8", "~8–10 weeks (first pods)",
        "Feb–Apr (spring planting)", "10–15 L pot",
        "Soak fresh chili seeds overnight, then sow 1 cm deep in warm potting mix. Germination in 1–2 weeks. Thin to 20–30 cm spacing. Flowering in 60–90 days; pick peppers from ~70 days onward.",
        PlantIconType.FRUIT),
    PlantInfo("Bell Pepper", "शिमला मिर्च", "ಬೆಲ್ಲ ಮೆಣಸಿನಕಾಯಿ", "మిరపకాయ", "Seed",
        "Full sun (6–8h)", "Loamy; pH 6.0–6.8", "~60–90 days",
        "Feb–Apr (summer crop)", "10–15 L pot",
        "Sow fresh pepper seeds 1–2 cm deep in warm soil. Germinate in 7–14 days. Thin seedlings to 30–45 cm. Harvest green or ripe fruits in 60–90 days.",
        PlantIconType.FRUIT),
    PlantInfo("Tomato", "टमाटर", "ಟೊಮಾಟೊ", "టమోట", "Seeds or Cuttings",
        "Full sun (6–8h)", "Loam; pH 6.5–7.0", "~60–80 days (first fruit)",
        "Oct–Dec transplant", "20–30 cm spacing with stakes",
        "Sow tomato seeds 1 cm deep in warm soil. Germinate in 5–10 days. Transplant seedlings 30 cm apart. Provide 6–8h sun. Harvest first ripe fruits in 60–80 days from transplant.",
        PlantIconType.FRUIT),
    PlantInfo("Spinach", "पालक", "—", "పాలకూర", "Seeds",
        "Partial sun (4–6h)", "Loam; pH 6.0–7.5", "~30 days (leaves)",
        "Oct–Dec (rabi)", "10–15 cm pot",
        "Sow spinach seeds 1–2 cm deep in moist soil. Germinate in 7–14 days. Thin to 5–10 cm. Provide partial sun to avoid bolting. Harvest leaves continuously from ~30 days onwards.",
        PlantIconType.LEAF),
    PlantInfo("Peas", "मटर", "ಹಡವಳ್ಳಿ", "బఠాణీలు", "Seeds",
        "Full sun (6–8h)", "Sandy loam; pH 6.0–7.0", "~55–70 days",
        "Oct–Dec (rabi)", "10–15 cm pot or bed",
        "Sow pea seeds 3–5 cm deep, 5–7 cm apart in rows. Provide partial support. Germinate in 7–10 days. Cool season (10–25°C). First pods in 55–70 days; harvest sweet pods then dry seeds.",
        PlantIconType.LEAF),
    PlantInfo("Cabbage", "पत्ता गोभी", "ಮುತ್ತುರು", "తక్కాల్", "Seeds; crown regrowth",
        "Full sun (6–8h)", "Loam; pH 6.0–7.5", "~70–90 days",
        "Oct–Dec (Rabi)", "20–30 L pot",
        "Sow cabbage seeds 1–2 cm deep, thin to 30 cm spacing. Harvest heads in 70–90 days. For regrowth, cut head 5 cm above root and plant stump in moist soil — small leaves will regrow.",
        PlantIconType.LEAF),
    PlantInfo("Mustard Greens", "सरसों का साग", "ಸಾಸಿವೆ ಸೊಪ್ಪು", "ఆవ పచ్చడి", "Seeds",
        "Full sun (4–6h)", "Loam; pH 6.0–7.0", "~30–40 days (leaves)",
        "Oct–Nov (rabi)", "10–15 cm pot",
        "Sow mustard seeds 1 cm deep; keep moist. Germinate in 3–5 days. Thin to ~10 cm. Harvest tender leaves in 30–40 days as greens.",
        PlantIconType.HERB),
    PlantInfo("French Beans", "सेम", "ಬೆಳೆಬೆಳೆ", "పప్పు", "Seeds",
        "Full sun (6–8h)", "Rich loam; pH 6.0–7.5", "~45–60 days",
        "Jun–Jul (kharif/summer)", "10–15 L pot with support",
        "Sow bean seeds 2–3 cm deep, spacing 10–15 cm. Keep warm (25–30°C). Germinate in 7–10 days. First pods in 45–60 days; harvest continued for 2–3 months.",
        PlantIconType.LEAF),
    PlantInfo("Lemongrass", "बरसीम घास", "ಗಂಡಕುಸುಮ", "ఎలా", "Stem cuttings",
        "Full sun (6–8h)", "Rich loam; pH 6.5–7.0", "~3–4 months (stalks)",
        "Feb–Apr (spring planting)", "15–20 cm pot",
        "Take 10–15 cm stalk bases and plant in moist soil. Keep warm in full sun. New tillers appear in ~2 weeks. Harvest stalks after 3–4 months.",
        PlantIconType.HERB),
    PlantInfo("Papaya", "पपीता", "ಪಾಪಿತ", "బొప్పాయి", "Seeds",
        "Full sun (6–8h)", "Light loam; pH 6.0–6.5", "~9–11 months",
        "Feb–May planting", "20–30 L pot",
        "Sow papaya seeds 1–2 cm deep in a large pot. Keep warm and moist. Germination in 2–3 weeks. Thin to one plant. Provide full sun. Trees fruit in 9–11 months; pick when greenish-yellow.",
        PlantIconType.FRUIT),
    PlantInfo("Pineapple", "अनानास", "ಅನಾನಸ್", "అనాస", "Top cutting",
        "Full sun (6–8h)", "Loam with compost; pH 5.5–6.5", "~24 months (first fruit)",
        "Year-round", "15–20 L pot",
        "Twist or cut off the leafy crown of a pineapple fruit. Remove lower leaves to expose ~2–3 cm stem. Plant in potting mix burying the stem. Keep warm and moist. Flowering by 12–18 months.",
        PlantIconType.FRUIT),
    PlantInfo("Pomegranate", "अनार", "ದಾಳಿಂಬೆ", "దాడిమ", "Seeds or Cuttings",
        "Full sun (6–8h)", "Light loam; pH 5.5–7.0", "~2–3 years",
        "Feb–Apr planting", "20–30 L pot",
        "Sow fresh seeds ~1 cm deep or plant 20–25 cm hardwood cuttings in sand-soil mix. Give full sun. Plants fruit in 2–3 years; pick reddish fruits in summer.",
        PlantIconType.FRUIT),
    PlantInfo("Guava", "अमरूद", "ಸೀತಾಫಲ", "జామ", "Seeds or Cuttings",
        "Full sun (6–8h)", "Loam; pH 5.5–7.5", "~2–3 years (seedlings)",
        "Apr–May planting", "15–20 L pot",
        "Sow seeds 2–3 cm deep in potting mix; germination in 2–3 weeks. Thin to one seedling/pot. Or plant 1-year-old stem cuttings vertically. Transplant outside after 6 months. Fruit in 2–3 years.",
        PlantIconType.FRUIT),
    PlantInfo("Lemon / Lime", "नींबू", "ನಿಂಬೆ", "నిమ్మ", "Seeds or Cuttings",
        "Full sun (6–8h)", "Loam; pH 5.5–6.5", "~3–5 years (seedlings)",
        "Jul–Sep planting", "15–20 L pot",
        "Plant seed 1 cm deep in potting mix; seedlings in 2–3 weeks. Fruit seedling in 3–5 years. Alternatively, graft budwood onto rootstock or plant a cutting. Harvest fruit ~6 months after flowering.",
        PlantIconType.FRUIT),
    PlantInfo("Mango", "आम", "ಮಾವು", "మామిడి", "Seed or Grafting",
        "Full sun (6–8h)", "Deep loam; pH 5.5–7.5", "~5–6 years (seedling)",
        "Mar–Jun planting", "50 L deep pot or orchard",
        "Germinate a cleaned seed 2–3 cm deep. Keep warm and moist. Grow in large pot for 2–3 years, then transplant. Mango from seed may flower in 5–6 years; grafted varieties fruit by 2–3 years.",
        PlantIconType.FRUIT),
    PlantInfo("Jackfruit", "कटहल", "ಹಲಸು", "పలకాయ", "Seeds or Suckers",
        "Full sun (6–8h)", "Deep loam; pH 6.0–7.5", "~3–4 years",
        "Feb–Mar planting", "50 L pot or large space",
        "Sow seeds 2–3 cm deep; germinate in 3 weeks. Or plant a rooted sucker. Keep soil wet and sunny. Transplant in 1–2 years. Fruit in 3–4 years. Harvest large jackfruit when mature by smell.",
        PlantIconType.FRUIT),
    PlantInfo("Cardamom", "इलायची", "ಏಲಕ್ಕಿ", "ఈలకాయ", "Rhizome (sucker)",
        "Partial sun (4–6h)", "Rich loam; pH 6.5–7.5", "~24–30 months",
        "Jun–Sep (rainy season)", "30–40 cm spacing",
        "Plant healthy cardamom suckers in well-drained shaded soil. Cover with 5 cm soil. Keep moist and shady. Shoots emerge in ~30 days. Plant reaches yield in 2–3 years, harvesting pods 8–10 months after flowering.",
        PlantIconType.HERB)
)

// ─── Colors ───────────────────────────────────────────────────────────────────

val HeaderGreen = Color(0xFF1A4032)
val AccentGreen = Color(0xFF5EC49A)
val IconBg      = Color(0xFFD6EDE1)
val IconStroke  = Color(0xFF2D7A52)
val CardBg      = Color(0xFFFFFFFF)
val PageBg      = Color(0xFFF0EDE6)
val DetailBg    = Color(0xFFFAFAF8)
val InstrBg     = Color(0xFFEDF7F0)
val InstrText   = Color(0xFF1A4032)
val LabelColor  = Color(0xFF6E7A6E)
val ValueColor  = Color(0xFF2A3D2E)
val PropBg      = Color(0xFFFFF3CD)
val PropText    = Color(0xFF7A5C00)
val LangTagBg   = Color(0xFFE8F0EA)
val LangTagText = Color(0xFF2D6B48)
val CountBg     = Color(0xFFD4EDDA)
val CountText   = Color(0xFF1A6634)
val DividerColor= Color(0xFFE8E4DE)

// ─── Icon composable ──────────────────────────────────────────────────────────

@Composable
fun PlantIcon(type: PlantIconType, modifier: Modifier = Modifier) {
    val emoji = when (type) {
        PlantIconType.ROOT  -> "🌱"
        PlantIconType.LEAF  -> "🍃"
        PlantIconType.HERB  -> "🌿"
        PlantIconType.BULB  -> "🧄"
        PlantIconType.FRUIT -> "🍎"
    }
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(IconBg),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 18.sp)
    }
}

// ─── Plant card ───────────────────────────────────────────────────────────────

@Composable
fun PlantCard(plant: PlantInfo) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "chevron"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // ── Header row ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { expanded = !expanded }
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlantIcon(type = plant.iconType)

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plant.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A2E1F),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = plant.instruction.take(60) + "…",
                        fontSize = 11.5.sp,
                        color = LabelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = IconStroke,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(rotation)
                )
            }

            // ── Expanded details ──
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DetailBg)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = DividerColor,
                        thickness = 0.5.dp
                    )

                    // Propagation badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PropBg)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(text = plant.propagation, fontSize = 11.sp, color = PropText, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Instruction box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(InstrBg)
                            .padding(10.dp)
                    ) {
                        Text(text = plant.instruction, fontSize = 12.sp, color = InstrText, lineHeight = 19.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Detail rows
                    DetailRow(label = "Sunlight", value = plant.sunlight)
                    DetailRow(label = "Soil", value = plant.soil)
                    DetailRow(label = "Harvest", value = plant.harvest)
                    DetailRow(label = "Season", value = plant.season)
                    DetailRow(label = "Container", value = plant.container)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Language tags
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(plant.hindi, plant.kannada, plant.telugu)
                            .filter { it != "—" }
                            .forEach { lang ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(LangTagBg)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = lang, fontSize = 10.sp, color = LangTagText, fontWeight = FontWeight.Medium)
                                }
                            }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            color = LabelColor,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp,
            modifier = Modifier.width(72.dp)
        )
        Text(text = value, fontSize = 12.sp, color = ValueColor, lineHeight = 17.sp)
    }
}

// ─── Search bar ───────────────────────────────────────────────────────────────

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 13.sp
            ),
            cursorBrush = SolidColor(Color.White),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text("Search food plants…", fontSize = 13.sp, color = Color.White.copy(alpha = 0.55f))
                }
                inner()
            }
        )
    }
}

// ─── Main screen ──────────────────────────────────────────────────────────────

@Composable
fun Regrow(onBack: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) foodPlants
        else foodPlants.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.hindi.contains(searchQuery, ignoreCase = true) ||
                    it.kannada.contains(searchQuery, ignoreCase = true) ||
                    it.propagation.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Sticky header ──
        Column(modifier = Modifier.background(HeaderGreen)) {

            // Back + title
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { onBack() }
                )
                Column {
                    Text(
                        text = "Regrowing Plants",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Learn how to grow plants using food from your vegetables",
                        fontSize = 12.sp,
                        color = AccentGreen
                    )
                }
            }

            // Search bar
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                SearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // ── Content ──
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                // Count badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CountBg)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${filtered.size} plant${if (filtered.size != 1) "s" else ""}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CountText
                    )
                }
            }
            itemsIndexed(filtered, key = { _, p -> p.name }) { _, plant ->
                PlantCard(plant = plant)
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
