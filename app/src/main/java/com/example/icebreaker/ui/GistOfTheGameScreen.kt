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
 * Composable screen that explains the rules and flow of the Icebreaker game.
 * Uses a scrollable column to accommodate the detailed textual content.
 */
@Composable
fun GistOfTheGameScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header: General Concept ───────────────────────────────────────
        Text(
            text = "How to: ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Icebreaker is designed to help you randomly pair 'Tops' and 'Bottoms' for your games or activities.",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // ── Rules: Safety and Gameplay ────────────────────────────────────
        Text(
            text = "What happens in the icebreaker game: ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "All implements must be used lightly as there is no negotiation or warmup.",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // ── Gameplay Flow ──────────────────────────────────────────────────
        Text(
            text = "A bottom’s name will be randomly selected and called to come forward.\nA Tops name is randomly selected\nThe bottom decides if they are comfortable playing with the Top.\nIf the bottom does not consent, another Top will be randomly selected.",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = "The Top is then called to come forward.\nThe bottom picks out a spanking implement.\nTypically the range is from 5-10 light strokes, the bottom can choose the double the strokes if desired.",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // ── Logistics: Ratio & Pacing ──────────────────────────────────────
        Text(
            text = "The ratio of Tops vs Bottoms can vary, so it may take longer to get thru all the names of one category while the other category is called a second time.",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // ── Equipment Information ──────────────────────────────────────────
        Text(
            text = "Types of toys: ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "paddles (of many variations), crops, canes & other implements. Hands are also acceptable. ",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
    }
}
