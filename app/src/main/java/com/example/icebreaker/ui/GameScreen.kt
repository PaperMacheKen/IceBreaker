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

/**
 * Visual styling constants consistent with InputScreen.
 */
private val TopColor    = Color(0xFF1976D2)   // Branding Blue
private val BottomColor = Color(0xFF388E3C)   // Branding Green

/**
 * The Game Screen handles the random pairing of participants.
 * It provides two panels (Tops & Bottoms) for independent selection and a way to confirm pairs.
 */
@Composable
fun GameScreen(
    viewModel: IcebreakerViewModel,
    onSwitchToInput: () -> Unit
) {
    // Collect active selections from the ViewModel
    val selectedTop    by viewModel.selectedTop.collectAsState()
    val selectedBottom by viewModel.selectedBottom.collectAsState()
    val scope          = rememberCoroutineScope()

    // ── Session State ─────────────────────────────────────────────────────
    // Flags indicating if there are no more unused names in a category
    var topExhausted    by remember { mutableStateOf(false) }
    var bottomExhausted by remember { mutableStateOf(false) }

    // Controls for confirmation dialogs
    var showClearTopUsedDialog    by remember { mutableStateOf(false) }
    var showClearBottomUsedDialog by remember { mutableStateOf(false) }

    // Effect: Reset exhaustion flags automatically if a name successfully appears
    LaunchedEffect(selectedTop)    { if (selectedTop    != null) topExhausted    = false }
    LaunchedEffect(selectedBottom) { if (selectedBottom != null) bottomExhausted = false }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Section: Interactive Selection Panels ─────────────────────────
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // ── LEFT PANEL: BOTTOMS ───────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(BottomColor.copy(alpha = 0.08f)) // Subtle background tint
                    .padding(12.dp)
            ) {
                // Header with "Clear Used" utility
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

                // Conditional UI: Reroll button appears only after selection
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BottomColor)
                    ) {
                        Text("Reroll")
                    }
                }

                // Central Area: Randomizer button or Chosen Name
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    when {
                        bottomExhausted -> {
                            // Displayed when the Bottoms list is empty of unused names
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "No more Bottoms\navailable!",
                                    textAlign  = TextAlign.Center,
                                    color      = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize   = 15.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                OutlinedButton(onClick = { viewModel.clearBottomUsed(); bottomExhausted = false }) {
                                    Text("Clear Used Bottoms")
                                }
                            }
                        }

                        selectedBottom == null -> {
                            // Primary call to action for selection
                            Button(
                                onClick = {
                                    scope.launch {
                                        val ok = viewModel.randomBottom()
                                        if (!ok) bottomExhausted = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BottomColor),
                                shape    = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.85f).height(80.dp)
                            ) {
                                Text("Random\nBottom", textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        else -> {
                            // Display the selected participant
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
                Spacer(Modifier.height(36.dp))
            }

            VerticalDivider(modifier = Modifier.fillMaxHeight())

            // ── RIGHT PANEL: TOPS ─────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(TopColor.copy(alpha = 0.08f))
                    .padding(12.dp)
            ) {
                // Header with "Clear Used" utility
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
                
                // Conditional UI: Reroll button
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
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TopColor)
                    ) {
                        Text("Reroll")
                    }
                }

                // Central Area: Randomizer button or Chosen Name
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
                                OutlinedButton(onClick = { viewModel.clearTopUsed(); topExhausted = false }) {
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
                                colors = ButtonDefaults.buttonColors(containerColor = TopColor),
                                shape    = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth(0.85f).height(80.dp)
                            ) {
                                Text("Random\nTop", textAlign = TextAlign.Center, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                Spacer(Modifier.height(36.dp))
            }
        }

        // ── Section: Global Navigation & Confirmation ─────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Navigates back to the management screen
            OutlinedButton(
                onClick  = { onSwitchToInput() },
                modifier = Modifier.weight(1f)
            ) {
                Text("← Input Mode")
            }

            // Confirms the pairing, marking participants as used
            if (selectedTop != null && selectedBottom != null) {
                Button(
                    onClick = { viewModel.acceptPair() },
                    colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Accept ✓", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // ── Interaction: Confirmation Dialogs ───────────────────────────────
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
