package com.example.icebreaker

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Enumeration of the different screens available in the application.
 * Each entry includes a user-friendly label for display in navigation components.
 */
enum class Screen(val label: String) {
    INPUT("Input Mode"),
    GAME("Game Mode"),
    GIST("Gist of the Game"),
    FUNCTIONS("App How-To"),
    ABOUT("App Info")
}

/**
 * The main entry point of the application.
 * Sets up the ViewModel, edge-to-edge display, and the root Composable.
 */
class MainActivity : ComponentActivity() {

    // ViewModel instance scoped to this Activity, handles application logic and data
    private val viewModel: IcebreakerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enables full-screen content that flows under system bars
        enableEdgeToEdge()
        setContent {
            // State for toggling between dark and light themes
            var darkTheme by remember { mutableStateOf(true) }
            
            // Root theme wrapper for the application
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

/**
 * Preview Composable for the Splash Screen.
 */
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    IcebreakerTheme(darkTheme = true) {
        SplashScreen(onTimeout = {})
    }
}

/**
 * Top-level Composable that manages the transition between the Splash Screen and Main Content.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IcebreakerApp(
    viewModel: IcebreakerViewModel,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    // Controls visibility of the splash screen
    var showSplash by remember { mutableStateOf(true) }

    // Smooth fade transition between Splash and Main Content
    AnimatedContent(
        targetState = showSplash,
        transitionSpec = {
            fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(animationSpec = tween(1000))
        },
        label = "SplashTransition"
    ) { splashActive ->
        if (splashActive) {
            // Splash screen uses a fixed dark theme for branding consistency
            IcebreakerTheme(darkTheme = true, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreen(onTimeout = { showSplash = false })
                }
            }
        } else {
            // Primary application shell once splash is complete
            MainAppContent(
                viewModel = viewModel,
                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}

/**
 * Displays branding information temporarily when the app starts.
 * @param onTimeout Callback triggered when the splash duration ends.
 */
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    // Delays for 2 seconds before proceeding to main content
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Branding logo
            Image(
                painter = painterResource(id = R.drawable.ic_icebreaker_logo),
                contentDescription = "Icebreaker Logo",
                modifier = Modifier.size(180.dp)
            )
            Spacer(Modifier.height(32.dp))
            // App name with specific blue branding color
            @Suppress("MagicNumber")
            Text(
                text = "Icebreaker",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
        }
        // Footer credit
        Text(
            text = "from PaperMacheKen",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * The main container for the application, featuring a navigation drawer and scaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    viewModel: IcebreakerViewModel,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    // Navigation and UI state management
    var currentScreen by remember { mutableStateOf(Screen.INPUT) }
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()
    val context       = LocalContext.current

    // Visibility flags for confirmation dialogs
    var showClearDbDialog          by remember { mutableStateOf(false) }
    var showCloseAppDialog         by remember { mutableStateOf(false) }

    // Side-navigation drawer container
    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                // Drawer Header
                Text(
                    "Icebreaker",
                    style    = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Dynamically generate navigation items from Screen enum
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
                    // Visual separation between core game and info screens
                    if (screen == Screen.GIST) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // ── Settings ──────────────────────────────────────────────────
                // Dark mode toggle within the drawer
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // ── Critical Actions ──────────────────────────────────────────
                // Option to wipe all user-entered names
                NavigationDrawerItem(
                    label = { Text("Clear Database", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showClearDbDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Option to exit the app and wipe data (for privacy)
                NavigationDrawerItem(
                    label = { Text("Close App", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showCloseAppDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        // Main scaffold with TopAppBar and content area
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
                        // Menu button to open the navigation drawer
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Content area where the selected screen is rendered
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

    // ── Confirmation Dialog: Clear Database ───────────────────────────────────
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

    // ── Confirmation Dialog: Close App ────────────────────────────────────────
    if (showCloseAppDialog) {
        AlertDialog(
            onDismissRequest = { showCloseAppDialog = false },
            title = { Text("Close App") },
            text  = {
                Text(
                    "The app will close and ALL data will be deleted from the database. " +
                    "This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllData()
                    showCloseAppDialog = false
                    (context as? Activity)?.finish()
                }) {
                    Text("Close and Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseAppDialog = false }) { Text("Cancel") }
            }
        )
    }
}
