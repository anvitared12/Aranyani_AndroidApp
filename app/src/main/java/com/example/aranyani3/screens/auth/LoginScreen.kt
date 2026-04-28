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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.aranyani3.R
import com.example.aranyani3.auth.AuthViewModel

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
//  Input field background colour  (pill fields)
// ─────────────────────────────────────────────
private val FieldBg = Color(0xFFDDE8CC)

// ─────────────────────────────────────────────
//  Login Screen
// ─────────────────────────────────────────────
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated && !uiState.isLoading) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Background image (background.png) ──
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )


        // ── Content ──
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingState()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 48.dp),
                verticalArrangement = Arrangement.Center
            ) {

                // ── Heading ──
                Text(
                    text = "Login",
                    color = Headline,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Subtitle ──
                Text(
                    text = "It's time to return to the soil! Log in\nto your account and keep growing.",
                    color = Color.Black,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(36.dp))

                // ── Error message ──
                uiState.errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(14.dp))
                    ErrorBanner(message = error)
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ── Login / Logout button ──
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (activity != null && !uiState.isLoading)
                                Brush.linearGradient(listOf(DeepForest, RichFern))
                            else
                                Brush.linearGradient(listOf(Sage, MintMist))
                        )
                        .then(
                            if (activity != null && !uiState.isLoading)
                                Modifier.clickableNoRipple {
                                    activity.let {
                                        if (uiState.isAuthenticated) authViewModel.logout(it)
                                        else authViewModel.login(it)
                                    }
                                }
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.isAuthenticated) "Logout" else "Login",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


@Composable
fun AranyaniBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        DecorativeBlobs()
        content()
    }
}

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
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(pulse)
                .offset(x = 120.dp, y = (-60).dp)
                .blur(60.dp)
                .background(Garden.copy(alpha = 0.18f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .blur(50.dp)
                .background(Harvest.copy(alpha = 0.12f), CircleShape)
        )
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
//  Loading State
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
//  Error banner
// ─────────────────────────────────────────────
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
            interactionSource = MutableInteractionSource(),
            indication = null,
            onClick = onClick
        )
    )