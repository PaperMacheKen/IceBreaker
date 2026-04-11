package com.example.icebreaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.icebreaker.ui.AboutScreen
import com.example.icebreaker.ui.AppFunctionsScreen
import com.example.icebreaker.ui.GameScreen
import com.example.icebreaker.ui.GistOfTheGameScreen
import com.example.icebreaker.ui.InputScreen
import com.example.icebreaker.ui.theme.IcebreakerTheme
import com.example.icebreaker.viewmodel.IcebreakerViewModel
import kotlinx.coroutines.launch

// ─── Navigation targets ───────────────────────────────────────────────────────
enum class Screen(val label: String) {
    INPUT("Input Mode"),
    GAME("Game Mode"),
    GIST("Gist of the Game"),
    FUNCTIONS("App Functions"),
    ABOUT("App Info")
}

class MainActivity : ComponentActivity() {

    private val viewModel: IcebreakerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(true) }
            IcebreakerTheme(darkTheme = darkTheme) {
                IcebreakerApp(
                    viewModel = viewModel,
                    darkTheme = darkTheme,
                    onThemeChange = { darkTheme = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IcebreakerApp(
    viewModel: IcebreakerViewModel,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var currentScreen by remember { mutableStateOf(Screen.INPUT) }
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()

    // Confirmation dialog flags
    var showClearDbDialog          by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Icebreaker",
                    style    = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Screen.entries.forEach { screen ->
                    NavigationDrawerItem(
                        label    = { Text(screen.label) },
                        selected = currentScreen == screen,
                        onClick  = {
                            currentScreen = screen
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Theme Toggle in Drawer
                ListItem(
                    headlineContent = { Text("Dark Mode") },
                    trailingContent = {
                        Switch(
                            checked = darkTheme,
                            onCheckedChange = { onThemeChange(it) }
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            currentScreen.label,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // "Clear Database"
                        TextButton(
                            onClick = { showClearDbDialog = true },
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Text(
                                "Clear\nDB",
                                fontSize   = 9.sp,
                                lineHeight = 11.sp,
                                color      = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    Screen.INPUT -> InputScreen(
                        viewModel      = viewModel,
                        onSwitchToGame = { currentScreen = Screen.GAME }
                    )
                    Screen.GAME  -> GameScreen(
                        viewModel       = viewModel,
                        onSwitchToInput = { currentScreen = Screen.INPUT }
                    )
                    Screen.GIST -> GistOfTheGameScreen()
                    Screen.FUNCTIONS -> AppFunctionsScreen()
                    Screen.ABOUT -> AboutScreen()
                }
            }
        }
    }

    // ── Clear Database confirmation ───────────────────────────────────────────
    if (showClearDbDialog) {
        AlertDialog(
            onDismissRequest = { showClearDbDialog = false },
            title = { Text("Clear Database") },
            text  = {
                Text(
                    "This will permanently delete ALL names from both the Tops and " +
                    "Bottoms databases. This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData()
                    currentScreen     = Screen.INPUT
                    showClearDbDialog = false
                }) {
                    Text("Delete Everything", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDbDialog = false }) { Text("Cancel") }
            }
        )
    }
}
