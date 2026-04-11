package com.example.icebreaker.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icebreaker.viewmodel.IcebreakerViewModel
import kotlinx.coroutines.launch

// Colours matching InputScreen
private val TopColor    = Color(0xFF1976D2)
private val BottomColor = Color(0xFF388E3C)

@Composable
fun GameScreen(
    viewModel: IcebreakerViewModel,
    onSwitchToInput: () -> Unit
) {
    val selectedTop    by viewModel.selectedTop.collectAsState()
    val selectedBottom by viewModel.selectedBottom.collectAsState()
    val scope          = rememberCoroutineScope()

    // Exhausted flags – set when a random attempt finds no available names
    var topExhausted    by remember { mutableStateOf(false) }
    var bottomExhausted by remember { mutableStateOf(false) }

    // Confirmation dialog flags
    var showClearTopUsedDialog    by remember { mutableStateOf(false) }
    var showClearBottomUsedDialog by remember { mutableStateOf(false) }

    // Reset exhausted flags when a new selection is made
    LaunchedEffect(selectedTop)    { if (selectedTop    != null) topExhausted    = false }
    LaunchedEffect(selectedBottom) { if (selectedBottom != null) bottomExhausted = false }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Main two-panel area ────────────────────────────────────────────
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // ── LEFT PANEL – Bottoms ───────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(BottomColor.copy(alpha = 0.08f))
                    .padding(12.dp)
            ) {
                // Header label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "BOTTOM",
                        color      = BottomColor,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp,
                        letterSpacing = 1.sp
                    )
                    TextButton(
                        onClick = { showClearBottomUsedDialog = true },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("Clear Used", fontSize = 10.sp, color = BottomColor)
                    }
                }

                if (selectedBottom != null && !bottomExhausted) {
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val ok = viewModel.rerollBottom()
                                if (!ok) bottomExhausted = true
                            }
                        },
                        border = BorderStroke(1.5.dp, BottomColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BottomColor
                        )
                    ) {
                        Text("Reroll")
                    }
                }

                // Center: button or name
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    when {
                        bottomExhausted -> {
                            // No available bottoms
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "No more Bottoms\navailable!",
                                    textAlign  = TextAlign.Center,
                                    color      = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 15.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = {
                                        viewModel.clearBottomUsed()
                                        bottomExhausted = false
                                    }
                                ) {
                                    Text("Clear Used Bottoms")
                                }
                            }
                        }

                        selectedBottom == null -> {
                            // Initial state
                            Button(
                                onClick = {
                                    scope.launch {
                                        val ok = viewModel.randomBottom()
                                        if (!ok) bottomExhausted = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BottomColor
                                ),
                                shape    = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(80.dp)
                            ) {
                                Text(
                                    "Random\nBottom",
                                    textAlign  = TextAlign.Center,
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        else -> {
                            // Name selected
                            Text(
                                text       = selectedBottom!!.name,
                                color      = BottomColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 26.sp,
                                textAlign  = TextAlign.Center
                            )
                        }
                    }
                }

                // Placeholder to keep layout stable
                Spacer(Modifier.height(36.dp))
            }

            VerticalDivider(modifier = Modifier.fillMaxHeight())

            // ── RIGHT PANEL – Tops ────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(TopColor.copy(alpha = 0.08f))
                    .padding(12.dp)
            ) {
                // Header label + top-of-panel Reroll
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "TOP",
                        color      = TopColor,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 13.sp,
                        letterSpacing = 1.sp
                    )
                    TextButton(
                        onClick = { showClearTopUsedDialog = true },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text("Clear Used", fontSize = 10.sp, color = TopColor)
                    }
                }
                
                if (selectedTop != null && !topExhausted) {
                    Spacer(Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val ok = viewModel.rerollTop()
                                if (!ok) topExhausted = true
                            }
                        },
                        border = BorderStroke(1.5.dp, TopColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TopColor
                        )
                    ) {
                        Text("Reroll")
                    }
                }

                // Center: button or name
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    when {
                        topExhausted -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "No more Tops\navailable!",
                                    textAlign  = TextAlign.Center,
                                    color      = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 15.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = {
                                        viewModel.clearTopUsed()
                                        topExhausted = false
                                    }
                                ) {
                                    Text("Clear Used Tops")
                                }
                            }
                        }

                        selectedTop == null -> {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val ok = viewModel.randomTop()
                                        if (!ok) topExhausted = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = TopColor
                                ),
                                shape    = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(80.dp)
                            ) {
                                Text(
                                    "Random\nTop",
                                    textAlign  = TextAlign.Center,
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        else -> {
                            Text(
                                text       = selectedTop!!.name,
                                color      = TopColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 26.sp,
                                textAlign  = TextAlign.Center
                            )
                        }
                    }
                }

                // Placeholder at bottom of right panel (keep layout symmetric)
                Spacer(Modifier.height(36.dp))
            }
        }

        // ── Bottom row: Accept or Back ─────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick  = { onSwitchToInput() },
                modifier = Modifier.weight(1f)
            ) {
                Text("← Input Mode")
            }

            if (selectedTop != null && selectedBottom != null) {
                Button(
                    onClick = { viewModel.acceptPair() },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Accept ✓", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // ── Clear Top Unavailable confirmation ────────────────────────────────────
    if (showClearTopUsedDialog) {
        AlertDialog(
            onDismissRequest = { showClearTopUsedDialog = false },
            title = { Text("Clear Top Unavailable") },
            text  = { Text("Make all Tops available for selection again?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearTopUsed()
                    showClearTopUsedDialog = false
                    topExhausted = false
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showClearTopUsedDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ── Clear Bottom Unavailable confirmation ─────────────────────────────────
    if (showClearBottomUsedDialog) {
        AlertDialog(
            onDismissRequest = { showClearBottomUsedDialog = false },
            title = { Text("Clear Bottom Unavailable") },
            text  = { Text("Make all Bottoms available for selection again?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearBottomUsed()
                    showClearBottomUsedDialog = false
                    bottomExhausted = false
                }) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showClearBottomUsedDialog = false }) { Text("Cancel") }
            }
        )
    }
}
