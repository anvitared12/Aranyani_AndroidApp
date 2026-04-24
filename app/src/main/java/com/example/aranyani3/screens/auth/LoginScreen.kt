package com.example.aranyani3.screens.auth

import android.app.Activity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aranyani3.auth.Auth0Manager
import com.example.aranyani3.auth.AuthViewModel
import com.example.aranyani3.auth.AuthViewModelFactory

// ─────────────────────────────────────────────
//  Design Tokens  (mirror AppColors from MainHome)
// ─────────────────────────────────────────────
private val DeepForest  = Color(0xFF2D4A1E)
private val RichFern    = Color(0xFF4A7A2F)
private val Garden      = Color(0xFF6B9E45)
private val Leaf        = Color(0xFF8FBF5E)
private val Sage        = Color(0xFFB5D48A)
private val MintMist    = Color(0xFFD8EAC0)
private val LinenLeaf   = Color(0xFFEBF0D6)
private val Cream       = Color(0xFFF5F0E8)
private val Harvest     = Color(0xFFE8893E)
private val Golden      = Color(0xFFF2B135)
private val Headline    = Color(0xFF1A2810)
private val Body        = Color(0xFF4D6E35)
private val Muted       = Color(0xFF7A9E5C)

// Legacy aliases used by the original composable helpers below
val AranyaniOlive = Body
val AranyaniDark  = Headline

private val bgGradient = Brush.verticalGradient(
    listOf(Cream, LinenLeaf, MintMist)
)
private val cardGradient = Brush.linearGradient(
    listOf(Color(0xFFF0EDE4), Color(0xFFE8F2DA))
)
private val buttonGradient = Brush.linearGradient(
    listOf(RichFern, Garden)
)

// ─────────────────────────────────────────────
//  Login Screen
// ─────────────────────────────────────────────
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,           // ← accept it from caller
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity


    val uiState by authViewModel.uiState.collectAsState()

    // Single effect — was duplicated before, that also caused double navigation
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated && !uiState.isLoading) {
            onLoginSuccess()
        }
    }

    AranyaniBackground {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                LoadingState()
            } else {
                LoginCard(
                    isAuthenticated = uiState.isAuthenticated,
                    errorMessage = uiState.errorMessage,
                    activityAvailable = activity != null && !uiState.isLoading,
                    onAction = {
                        activity?.let {
                            if (uiState.isAuthenticated) authViewModel.logout(it)
                            else authViewModel.login(it)
                        }
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Background  (replaces old AranyaniBackground)
// ─────────────────────────────────────────────
@Composable
fun AranyaniBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // Decorative blurred blobs for depth
        DecorativeBlobs()
        content()
    }
}

// Soft blurred circles in the background — new composable
@Composable
private fun DecorativeBlobs() {
    val infiniteTransition = rememberInfiniteTransition(label = "blob")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Top-right large blob
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(pulse)
                .offset(x = 120.dp, y = (-60).dp)
                .blur(60.dp)
                .background(Garden.copy(alpha = 0.18f), CircleShape)
        )
        // Bottom-left blob
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .blur(50.dp)
                .background(Harvest.copy(alpha = 0.12f), CircleShape)
        )
        // Centre accent blob
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.Center)
                .offset(y = 100.dp)
                .blur(40.dp)
                .background(MintMist.copy(alpha = 0.35f), CircleShape)
        )
    }
}

// ─────────────────────────────────────────────
//  Loading State  — new composable
// ─────────────────────────────────────────────
@Composable
private fun LoadingState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MintMist, CircleShape)
                .border(1.dp, Sage, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = RichFern,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(
            text = "Preparing your garden...",
            color = Body,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────
//  Main Login Card  — new composable
// ─────────────────────────────────────────────
@Composable
private fun LoginCard(
    isAuthenticated: Boolean,
    errorMessage: String?,
    activityAvailable: Boolean,
    onAction: () -> Unit,
) {
    AranyaniGlassCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Top leaf decoration row ──
            LeafDecorationRow()

            Spacer(modifier = Modifier.height(20.dp))

            // ── Icon bubble ──
            AranyaniIconBubble(
                icon = Icons.Default.Spa,
                size = 112.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── App name chip ──
            AppNameChip()

            Spacer(modifier = Modifier.height(16.dp))

            // ── Heading ──
            Text(
                text = if (isAuthenticated) "Welcome Back!" else "Welcome to Aranyani",
                color = Headline,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp,
                lineHeight = 34.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Subtitle ──
            Text(
                text = if (isAuthenticated)
                    "Your plant space is ready."
                else
                    "Login to continue your\nplant care journey.",
                color = Muted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            // ── Error message ──
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                ErrorBanner(message = error)
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Feature pills ──
            if (!isAuthenticated) {
                FeaturePillsRow()
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── CTA Button ──
            AranyaniPrimaryButton(
                text = if (isAuthenticated) "Logout" else "Login",
                icon = if (isAuthenticated) Icons.Default.Logout else Icons.Default.Login,
                enabled = activityAvailable,
                modifier = Modifier.fillMaxWidth(),
                onClick = onAction
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Footer note ──
            Text(
                text = "🌱  Your garden data is safe & private",
                color = Muted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Glass Card  (replaces old AranyaniGlassCard)
// ─────────────────────────────────────────────
@Composable
fun AranyaniGlassCard(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(cardGradient)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(Sage.copy(0.6f), MintMist.copy(0.4f))),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

// ─────────────────────────────────────────────
//  Icon Bubble  (replaces old AranyaniIconBubble)
// ─────────────────────────────────────────────
@Composable
fun AranyaniIconBubble(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    size: Dp = 96.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale"
    )

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .background(
                brush = Brush.radialGradient(listOf(MintMist, LinenLeaf)),
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(listOf(Garden, Leaf)),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner glow ring
        Box(
            modifier = Modifier
                .size(size * 0.78f)
                .background(Color.White.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RichFern,
                modifier = Modifier.size(size * 0.4f)
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Primary Button  (replaces old AranyaniPrimaryButton)
// ─────────────────────────────────────────────
@Composable
fun AranyaniPrimaryButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (enabled) buttonGradient
                else Brush.linearGradient(listOf(Sage, MintMist))
            )
            .border(
                width = 1.dp,
                color = if (enabled) Garden.copy(0.4f) else Sage.copy(0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .then(
                if (enabled) Modifier.clickableNoRipple(onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) Color.White else Muted,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = text,
                color = if (enabled) Color.White else Muted,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.2).sp
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Small helper composables  — all new
// ─────────────────────────────────────────────

// Decorative leaf emoji row at top of card
@Composable
private fun LeafDecorationRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Sage, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text("🌿", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Sage, CircleShape)
            )
        }
    }
}

// Chip showing the app category
@Composable
private fun AppNameChip() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(RichFern.copy(alpha = 0.12f))
            .border(0.5.dp, Garden.copy(0.35f), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 5.dp)
    ) {
        Text(
            text = "🌱  PLANT CARE · ARANYANI",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Body,
            letterSpacing = 1.sp
        )
    }
}

// Three feature highlight pills
@Composable
private fun FeaturePillsRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        FeaturePill(emoji = "🔍", label = "Scan",    modifier = Modifier.weight(1f))
        FeaturePill(emoji = "🩺", label = "Diagnose", modifier = Modifier.weight(1f))
        FeaturePill(emoji = "📅", label = "Remind",  modifier = Modifier.weight(1f))
    }
}

@Composable
private fun FeaturePill(emoji: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MintMist.copy(alpha = 0.6f))
            .border(0.5.dp, Sage.copy(0.5f), RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(emoji, fontSize = 18.sp)
        Text(
            text = label,
            fontSize = 11.sp,
            color = Body,
            fontWeight = FontWeight.Medium
        )
    }
}

// Error banner
@Composable
private fun ErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF0E8))
            .border(0.5.dp, Color(0xFFC96B3A).copy(0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("⚠️", fontSize = 14.sp)
            Text(
                text = message,
                color = Color(0xFFC96B3A),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Utility — clickable without ripple
// ─────────────────────────────────────────────
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickable(
            interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource(),
            indication = null,
            onClick = onClick
        )
    )