package com.example.icebreaker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icebreaker.data.Person
import com.example.icebreaker.viewmodel.IcebreakerViewModel

/**
 * Visual styling constants for the participant lists.
 */
private val TopColor       = Color(0xFF1976D2)   // Branding Blue for Tops
private val BottomColor    = Color(0xFF388E3C)   // Branding Green for Bottoms
private val UsedTextColor  = Color(0xFFAAAAAA)   // Muted grey for used participants

/**
 * Identifier used to distinguish between the two participant lists in logic.
 */
private enum class ListType { TOP, BOTTOM }

/**
 * The Input Screen allows users to manage the lists of "Tops" and "Bottoms".
 * Features name entry, list visualization, and editing/removal of entries.
 */
@Composable
fun InputScreen(
    viewModel: IcebreakerViewModel,
    onSwitchToGame: () -> Unit
) {
    // Collect lists from the ViewModel as Compose state
    val tops    by viewModel.tops.collectAsState()
    val bottoms by viewModel.bottoms.collectAsState()

    // ── Input State ───────────────────────────────────────────────────────
    var nameText    by remember { mutableStateOf("") }
    var addToTop    by remember { mutableStateOf(false) }
    var addToBottom by remember { mutableStateOf(false) }
    var showError   by remember { mutableStateOf(false) }

    // ── Dialog & Context Menu State ───────────────────────────────────────
    // Controls the long-press options menu
    var contextPerson   by remember { mutableStateOf<Person?>(null) }
    var contextListType by remember { mutableStateOf(ListType.TOP) }
    var showContextMenu by remember { mutableStateOf(false) }

    // Controls the name editing dialog
    var editingPerson   by remember { mutableStateOf<Person?>(null) }
    var editingListType by remember { mutableStateOf(ListType.TOP) }
    var editText        by remember { mutableStateOf("") }
    var showEditDialog  by remember { mutableStateOf(false) }

    // Controls the deletion confirmation dialog
    var deletingPerson   by remember { mutableStateOf<Person?>(null) }
    var deletingListType by remember { mutableStateOf(ListType.TOP) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        // ── Section: Participant Name Entry ───────────────────────────────
        OutlinedTextField(
            value = nameText,
            onValueChange = { nameText = it; showError = false },
            label = { Text("Enter a name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = showError
        )
        if (showError) {
            Text(
                text = "Select Top and/or Bottom before adding.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Section: Category Selection & Add Button ─────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Toggle for adding the name to the Tops list
            FilterChip(
                selected = addToTop,
                onClick  = { addToTop = !addToTop; showError = false },
                label    = { Text("Top") },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TopColor,
                    selectedLabelColor     = Color.White
                )
            )
            Spacer(Modifier.width(8.dp))
            // Toggle for adding the name to the Bottoms list
            FilterChip(
                selected = addToBottom,
                onClick  = { addToBottom = !addToBottom; showError = false },
                label    = { Text("Bottom") },
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BottomColor,
                    selectedLabelColor     = Color.White
                )
            )
            Spacer(Modifier.width(12.dp))
            // Button to persist the entry to the database
            Button(
                onClick = {
                    val trimmed = nameText.trim()
                    if (trimmed.isEmpty() || (!addToTop && !addToBottom)) {
                        showError = true
                        return@Button
                    }
                    if (addToTop)    viewModel.addTop(trimmed)
                    if (addToBottom) viewModel.addBottom(trimmed)
                    nameText    = ""
                    addToTop    = false
                    addToBottom = false
                    showError   = false
                }
            ) {
                Text("Add")
            }
        }

        Spacer(Modifier.height(12.dp))

        // ── Section: List Headers ──────────────────────────────────────────
        Row(Modifier.fillMaxWidth()) {
            Text(
                "Tops (${tops.size})",
                color      = TopColor,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                modifier   = Modifier.weight(1f)
            )
            Text(
                "Bottoms (${bottoms.size})",
                color      = BottomColor,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
                modifier   = Modifier.weight(1f)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // ── Section: Parallel Participant Lists ───────────────────────────
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Scrollable list of Tops
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
            ) {
                items(tops, key = { it.id }) { person ->
                    PersonListItem(
                        person = person,
                        tintColor = TopColor,
                        onLongPress = {
                            contextPerson   = person
                            contextListType = ListType.TOP
                            showContextMenu = true
                        }
                    )
                }
            }

            VerticalDivider(modifier = Modifier.fillMaxHeight())

            // Scrollable list of Bottoms
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(start = 4.dp)
            ) {
                items(bottoms, key = { it.id }) { person ->
                    PersonListItem(
                        person = person,
                        tintColor = BottomColor,
                        onLongPress = {
                            contextPerson   = person
                            contextListType = ListType.BOTTOM
                            showContextMenu = true
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Navigation: Switch to Game Mode ──────────────────────────────
        Button(
            onClick   = onSwitchToGame,
            modifier  = Modifier.fillMaxWidth(),
            colors    = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Switch to Game Mode")
        }
    }

    // ── Interaction: Long-press context menu ──────────────────────────────
    if (showContextMenu && contextPerson != null) {
        AlertDialog(
            onDismissRequest = { showContextMenu = false },
            title = { Text(contextPerson!!.name) },
            text  = { Text("What would you like to do?") },
            confirmButton = {
                // Initiates name editing
                TextButton(onClick = {
                    editingPerson   = contextPerson
                    editingListType = contextListType
                    editText        = contextPerson!!.name
                    showContextMenu = false
                    showEditDialog  = true
                }) { Text("Edit") }
            },
            dismissButton = {
                // Initiates record deletion
                TextButton(onClick = {
                    deletingPerson   = contextPerson
                    deletingListType = contextListType
                    showContextMenu  = false
                    showDeleteDialog = true
                }) { Text("Remove", color = MaterialTheme.colorScheme.error) }
            }
        )
    }

    // ── Interaction: Edit dialog ─────────────────────────────────────────
    if (showEditDialog && editingPerson != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Name") },
            text  = {
                OutlinedTextField(
                    value         = editText,
                    onValueChange = { editText = it },
                    label         = { Text("Name") },
                    singleLine    = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editText.isNotBlank()) {
                        val p = editingPerson!!
                        if (editingListType == ListType.TOP)
                            viewModel.updateTop(p.id, editText.trim())
                        else
                            viewModel.updateBottom(p.id, editText.trim())
                        showEditDialog = false
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ── Interaction: Delete confirmation dialog ──────────────────────────
    if (showDeleteDialog && deletingPerson != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Name") },
            text  = {
                Text("Remove \"${deletingPerson!!.name}\" from the ${
                    if (deletingListType == ListType.TOP) "Tops" else "Bottoms"
                } list?")
            },
            confirmButton = {
                TextButton(onClick = {
                    val p = deletingPerson!!
                    if (deletingListType == ListType.TOP) viewModel.deleteTop(p.id)
                    else viewModel.deleteBottom(p.id)
                    showDeleteDialog = false
                }) { Text("Remove", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

/**
 * Renders a single row representing a participant.
 * Displays their name and a status indicator (dot).
 * Long-pressing triggers management options.
 */
@Composable
private fun PersonListItem(
    person: Person,
    tintColor: Color,
    onLongPress: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(person.id) {
                // Detects a long press gesture to open the context menu
                detectTapGestures(onLongPress = { onLongPress() })
            }
            .padding(vertical = 6.dp, horizontal = 4.dp)
    ) {
        // Status indicator dot (coloured if available, grey if used)
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (person.used) UsedTextColor else tintColor,
                    shape = RoundedCornerShape(4.dp)
                )
        )
        Spacer(Modifier.width(8.dp))
        // Participant name (strikethrough and muted if used)
        Text(
            text            = person.name,
            color           = if (person.used) UsedTextColor else Color.Unspecified,
            textDecoration  = if (person.used) TextDecoration.LineThrough else TextDecoration.None,
            fontSize        = 14.sp,
            modifier        = Modifier.weight(1f)
        )
    }
}
