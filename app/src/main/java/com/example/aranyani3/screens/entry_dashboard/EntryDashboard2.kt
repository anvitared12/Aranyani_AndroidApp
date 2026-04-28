package com.example.aranyani3.screens.entry_dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aranyani3.R

@Composable
fun Entry_Dashboard2(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // 🌿 Background Image
        Image(
            painter = painterResource(id = R.drawable.second), // your image
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 🌑 Optional dark overlay (for better text visibility)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        // 📝 Text Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {

            Text(
                text = "Regrow",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                color = Color(0xFF7AFF8E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Grow food \nfrom your kitchen ",
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(160.dp))
        }

        // 🔘 Button
        Button(
            onClick = {navController.navigate("dashboard3")},
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .height(50.dp)
                .width(200.dp)
        ) {
            Text(
                text = "NEXT",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

