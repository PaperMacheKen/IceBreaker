package com.example.icebreaker.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icebreaker.R

@Composable
fun AboutScreen() {
    val uriHandler = LocalUriHandler.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // ── Logo ──────────────────────────────────────────────────────────
        // Replace ic_icebreaker_logo with your actual image drawable if needed
        Image(
            painter            = painterResource(id = R.drawable.ic_icebreaker_logo),
            contentDescription = "Icebreaker Logo",
            modifier           = Modifier.size(180.dp)
        )

        Spacer(Modifier.height(32.dp))

        // ── App name ─────────────────────────────────────────────────────
        Text(
            text       = "Icebreaker",
            fontSize   = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            color      = Color(0xFF1976D2)
        )

        Spacer(Modifier.height(16.dp))

        // ── Credit ────────────────────────────────────────────────────────
        Text(
            text      = "From: PaperMacheKen",
            fontSize  = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // ── Link ──────────────────────────────────────────────────────────
        Text(
            text           = "fetlife.com/PaperMacheKen",
            fontSize       = 16.sp,
            textAlign      = TextAlign.Center,
            color          = Color(0xFF1976D2),
            textDecoration = TextDecoration.Underline,
            modifier       = Modifier.clickable {
                uriHandler.openUri("https://fetlife.com/PaperMacheKen")
            }
        )
    }
}
