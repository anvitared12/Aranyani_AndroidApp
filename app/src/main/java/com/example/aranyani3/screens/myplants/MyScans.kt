package com.example.aranyani3.screens.myplants

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aranyani2.ui_screens.screens.MyCustomFont
import com.example.aranyani3.models.ScanItem
import com.example.aranyani3.viewmodel.ScanHistoryViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val PageBg       = Color(0xFFFFFFFF)
private val CardWhite    = Color(0xFFDCE775)
private val ChipBg       = Color(0xFFE5E8DF)
private val AccentGreen  = Color(0xFF8FAF7A)
private val TextDark     = Color(0xFF1A1A1A)
private val TextMid      = Color(0xFF5A6478)
private val TextLight    = Color(0xFF9AAABB)
private val AlertRed     = Color(0xFFE05555)
private val AlertBg      = Color(0xFFFFF0F0)
private val GreenBadgeBg = Color(0xFFDFF0E3)
private val BlueBadgeBg  = Color(0xFFE5EEFF)
private val HeaderText   = Color(0xFF000000)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyScans(
    viewModel: ScanHistoryViewModel,
    onViewPlants: () -> Unit          // ← the missing parameter
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadScans() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Text(
                text = "My Scans",
                fontFamily = MyCustomFont,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = HeaderText,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Your plant history",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = HeaderText,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading scans...", color = Color(0xFFCDD9C0), fontSize = 14.sp)
                        }
                    }
                }

                uiState.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("⚠️", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(uiState.error!!, color = AlertRed, fontSize = 14.sp)
                            }
                        }
                    }
                }

                uiState.scans.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(0.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(36.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🌱", fontSize = 52.sp)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    "No scans yet",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Start scanning plants!",
                                    fontSize = 14.sp,
                                    color = TextMid
                                )
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.scans, key = { it.id }) { scan ->
                            ScanCard(
                                scan = scan,
                                onDelete = { viewModel.deleteScan(scan.id) },
                                onViewPlants = if (scan.scan_type == "garden") onViewPlants else null
                            )
                        }
                        item { Spacer(modifier = Modifier.height(24.dp)) }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScanCard(
    scan: ScanItem,
    onDelete: () -> Unit,
    onViewPlants: (() -> Unit)? = null
) {
    if (scan.scan_type == "garden") {
        android.util.Log.d("ScanDebug", "name='${scan.name}' | type='${scan.scan_type}'")
    }

    val (gradientColors, orbTopColor, orbBottomColor) = when (scan.scan_type) {
        "plant" -> Triple(
            listOf(Color(0xFFF0FAE8), Color(0xFFE0F0D0)),
            listOf(Color(0xFFA8D878), Color(0xFF6FB84A)),
            listOf(Color(0xFFC8EBB0), Color(0xFF8FCF60))
        )
        "disease" -> Triple(
            listOf(Color(0xFFFFF2F2), Color(0xFFFDE0E0)),
            listOf(Color(0xFFF4A0A0), Color(0xFFE05555)),
            listOf(Color(0xFFF8C0C0), Color(0xFFE87070))
        )
        "garden" -> Triple(
            listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE)),
            listOf(Color(0xFF93C5FD), Color(0xFF3B82F6)),
            listOf(Color(0xFFBFDBFE), Color(0xFF60A5FA))
        )
        else -> Triple(
            listOf(Color(0xFFF5F5F5), Color(0xFFEAEAEA)),
            listOf(Color(0xFFCCCCCC), Color(0xFF999999)),
            listOf(Color(0xFFDDDDDD), Color(0xFFAAAAAA))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradientColors))
    ) {
        // Top-left orb
        Box(
            modifier = Modifier
                .size(72.dp)
                .offset(x = (-22).dp, y = (-22).dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(orbTopColor))
                .alpha(0.55f)
        )
        // Bottom-right orb
        Box(
            modifier = Modifier
                .size(88.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 20.dp, y = 26.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(orbBottomColor))
                .alpha(0.55f)
        )

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Scan image ──
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = scan.image_url,
                    contentDescription = scan.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // ── Text + badges column ──
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scan.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = when (scan.scan_type) {
                        "plant"   -> Color(0xFF27500A)
                        "disease" -> Color(0xFF791F1F)
                        "garden"  -> Color(0xFF0C447C)
                        else      -> Color(0xFF1A1A1A)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(5.dp))
                ScanTypeBadge(type = scan.scan_type, name = scan.name)
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = formatDate(scan.created_at),
                    fontSize = 11.sp,
                    color = when (scan.scan_type) {
                        "plant"   -> Color(0xFF9CCC65)
                        "disease" -> Color(0xFFEF5350)
                        "garden"  -> Color(0xFF4FC3F7)
                        else      -> Color(0xFFAAAAAA)
                    }
                )

                // ── "View Plants" button — only for garden scans ──
                if (scan.scan_type == "garden" && onViewPlants != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        onClick = onViewPlants,
                        color = Color(0xFF3B82F6).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "🌱 View Plants",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0C447C),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ── Delete button ──
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.55f))
                    .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFA32D2D),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ScanTypeBadge(type: String, name: String = "") {
    val (label, bgColor, textColor) = when (type) {
        "plant" -> Triple("🌿 Plant", GreenBadgeBg, Color(0xFF2E7D32))
        "disease" -> Triple("🔬 Disease", AlertBg, AlertRed)
        "garden" -> {
            val potCount = extractPotCount(name)
            val lbl = if (potCount != null) "🪴 Garden • $potCount pots" else "🪴 Garden"
            Triple(lbl, BlueBadgeBg, Color(0xFF3A6BD4))
        }
        else -> Triple(type, ChipBg, TextMid)
    }

    Surface(color = bgColor, shape = RoundedCornerShape(10.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

private fun extractPotCount(name: String): Int? =
    Regex("""(\d+)\s+pots?""", RegexOption.IGNORE_CASE).find(name)?.groupValues?.get(1)?.toIntOrNull()

@Composable
fun PotCountBadge(count: Int) {
    Surface(color = ChipBg, shape = RoundedCornerShape(10.dp)) {
        Text(
            text = "🪣 $count pots",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextMid,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(isoDate: String): String {
    return try {
        val instant = Instant.parse(isoDate)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        isoDate
    }
}