package com.example.icebreaker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable screen providing a quick-start guide on how to use the app's features.
 */
@Composable
fun AppFunctionsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header: Functionality Overview ───────────────────────────────
        Text(
            text = "App functions - How to:",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // ── Feature Breakdown ─────────────────────────────────────────────
        BulletPoint("Go to Input Mode to add names to the Tops and Bottoms lists.")
        BulletPoint("Switch to Game Mode to start pairing.")
        BulletPoint("Tap 'Random Top' and 'Random Bottom' to get a pair.")
        BulletPoint("Use 'Reroll' if you want to try a different name.")
        BulletPoint("Once you're happy with a pair, tap 'Accept' to mark them as used and keep track of who has played.")
        BulletPoint("If you run out of names, you can 'Clear Used' to start over with the same list, or 'Clear DB' to start completely fresh.")
    }
}

/**
 * Helper component for displaying a bulleted list item.
 */
@Composable
private fun BulletPoint(text: String) {
    Row(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(text = "• ", fontWeight = FontWeight.Bold)
        Text(text = text)
    }
}
