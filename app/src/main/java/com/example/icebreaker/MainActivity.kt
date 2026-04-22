package com.example.icebreaker

import android.app.Activity
import android.content.Intent
import android.net.Uri
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

// ─── Navigation targets ───────────────────────────────────────────────────────
enum class Screen(val label: String) {
    INPUT("Input Mode"),
    GAME("Game Mode"),
    GIST("Gist of the Game"),
    FUNCTIONS("App How-To"),
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

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    IcebreakerTheme(darkTheme = true) {
        SplashScreen(onTimeout = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IcebreakerApp(
    viewModel: IcebreakerViewModel,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var showSplash by remember { mutableStateOf(true) }

    AnimatedContent(
        targetState = showSplash,
        transitionSpec = {
            fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(animationSpec = tween(1000))
        },
        label = "SplashTransition"
    ) { splashActive ->
        if (splashActive) {
            // Splash screen is always dark themed
            IcebreakerTheme(darkTheme = true, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreen(onTimeout = { showSplash = false })
                }
            }
        } else {
            MainAppContent(
                viewModel = viewModel,
                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
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
            Image(
                painter = painterResource(id = R.drawable.ic_icebreaker_logo),
                contentDescription = "Icebreaker Logo",
                modifier = Modifier.size(180.dp)
            )
            Spacer(Modifier.height(32.dp))
            @Suppress("MagicNumber")
            Text(
                text = "Icebreaker",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    viewModel: IcebreakerViewModel,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var currentScreen by remember { mutableStateOf(Screen.INPUT) }
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()
    val context       = LocalContext.current

    // Confirmation dialog flags
    var showClearDbDialog          by remember { mutableStateOf(false) }
    var showCloseAppDialog         by remember { mutableStateOf(false) }

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
                    // Divider between GIST and FUNCTIONS
                    if (screen == Screen.GIST) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
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

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Close App Item
                NavigationDrawerItem(
                    label = { Text("Close App", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showCloseAppDialog = true
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(Modifier.weight(1f))

                // App Version and Update Notification
                val isUpdateAvailable by viewModel.isUpdateAvailable.collectAsState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/PaperMacheKen/IceBreaker/releases")
                            )
                            context.startActivity(intent)
                        }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isUpdateAvailable) {
                        Text(
                            text = "New release available",
                            color = Color(0xFF00E676), // Bright Green
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(
                        text = "Version ${viewModel.currentVersion}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
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

    // ── Close App confirmation ───────────────────────────────────────────────
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
