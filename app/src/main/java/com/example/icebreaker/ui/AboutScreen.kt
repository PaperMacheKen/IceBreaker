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

/**
 * Composable that displays the "About" screen, featuring the app logo,
 * name, developer credits, and external project links.
 */
@Composable
fun AboutScreen() {
    // Provides access to open URIs in the system's default browser
    val uriHandler = LocalUriHandler.current

    // Root layout container: a full-screen Column with centered content
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // ── App Logo ──────────────────────────────────────────────────────
        // Displays the application's branding image
        Image(
            painter            = painterResource(id = R.drawable.ic_icebreaker_logo),
            contentDescription = "Icebreaker Logo",
            modifier           = Modifier.size(180.dp)
        )

        Spacer(Modifier.height(32.dp))

        // ── Application Name ──────────────────────────────────────────────
        // Large bold text for the app title
        Text(
            text       = "Icebreaker",
            fontSize   = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            color      = Color(0xFF1976D2)
        )

        Spacer(Modifier.height(16.dp))

        // ── Developer Credits ─────────────────────────────────────────────
        // Attribution to the creator
        Text(
            text      = "From: PaperMacheKen",
            fontSize  = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        // ── External Links ────────────────────────────────────────────────
        
        // Clickable link to the developer's Fetlife profile
        Text(
            text           = "Fetlife",
            fontSize       = 16.sp,
            textAlign      = TextAlign.Center,
            color          = Color(0xFF1976D2),
            textDecoration = TextDecoration.Underline,
            modifier       = Modifier.clickable {
                uriHandler.openUri("https://fetlife.com/PaperMacheKen")
            }
        )

        Spacer(Modifier.height(8.dp))

        // Clickable link to the Project's GitHub repository
        Text(
            text           = "Project Github",
            fontSize       = 16.sp,
            textAlign      = TextAlign.Center,
            color          = Color(0xFF1976D2),
            textDecoration = TextDecoration.Underline,
            modifier       = Modifier.clickable {
                uriHandler.openUri("https://github.com/PaperMacheKen/IceBreaker")
            }
        )
    }
}
