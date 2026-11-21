package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.Draw
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.BookmarkedPassagesSheet
import com.example.ui.components.OutlineInspectorSheet
import com.example.ui.components.ProfileCalibrationSheet
import com.example.ui.components.StenoTopBar
import com.example.ui.screens.CorpusScreen
import com.example.ui.screens.DictateScreen
import com.example.ui.screens.DraftScreen
import com.example.ui.screens.SymbolsScreen
import com.example.ui.theme.LocalStenoColors
import com.example.ui.theme.PitmanStenoTheme
import com.example.ui.theme.ThemeAppearance
import com.example.ui.viewmodel.StenoViewModel
import java.util.Locale
import com.google.android.gms.ads.MobileAds

private const val EASYPAISA_SUPPORT_NUMBER = "03319031146"

object StenoRoutes {
    const val ROUTE_DICTATE = "dictate"
    const val ROUTE_SYMBOLS = "symbols"
    const val ROUTE_DRAFT = "draft"
    const val ROUTE_CORPUS = "corpus"
}

private data class NavDestinationItem(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    private val adsReady = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PitmanStenoApp(adsReady = adsReady.value)
        }
        // Wait for the Google Mobile Ads SDK before requesting any ad.
        MobileAds.initialize(this) {
            runOnUiThread { adsReady.value = true }
        }
    }
}

@Composable
fun PitmanStenoApp(
    adsReady: Boolean,
    viewModel: StenoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val systemDark = isSystemInDarkTheme()
    val resolvedDark = when (uiState.prefs.themeAppearance) {
        ThemeAppearance.SYSTEM -> systemDark
        ThemeAppearance.LIGHT -> false
        ThemeAppearance.DARK -> true
    }

    PitmanStenoTheme(
        darkTheme = resolvedDark,
        highContrast = uiState.prefs.highContrastMode
    ) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: StenoRoutes.ROUTE_DICTATE
    val snackbarHostState = remember { SnackbarHostState() }
    var showSupportDialog by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val extendedColors = LocalStenoColors.current
    val interstitialController = remember(context) { (context as? Activity)?.let { InterstitialAdController(it) } }
    LaunchedEffect(interstitialController, adsReady) {
        if (adsReady) interstitialController?.load()
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.loadCourtAudioUri(uri)
        }
    }

    val voiceRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val transcript = matches?.firstOrNull()
            if (!transcript.isNullOrBlank()) {
                viewModel.appendVoiceTranscript(transcript)
            }
        }
    }

    fun launchVoiceDictation() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Dictate legal passage or judgment...")
        }
        val resolved = intent.resolveActivity(context.packageManager)
        if (resolved != null) {
            voiceRecognizerLauncher.launch(intent)
        } else {
            viewModel.showMessage("Voice speech recognizer unavailable on this device; use Draft & Custom tab.")
        }
    }

    LaunchedEffect(uiState.snackbarMessage, uiState.ttsState.statusMessage, uiState.audioState.errorMessage) {
        val msg = uiState.snackbarMessage ?: uiState.ttsState.statusMessage ?: uiState.audioState.errorMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val navItems = listOf(
        NavDestinationItem(
            route = StenoRoutes.ROUTE_DICTATE,
            labelRes = R.string.nav_dictate,
            selectedIcon = Icons.Filled.GraphicEq,
            unselectedIcon = Icons.Outlined.GraphicEq,
            testTag = "nav_dictate"
        ),
        NavDestinationItem(
            route = StenoRoutes.ROUTE_SYMBOLS,
            labelRes = R.string.nav_symbols,
            selectedIcon = Icons.Filled.Draw,
            unselectedIcon = Icons.Outlined.Draw,
            testTag = "nav_symbols"
        ),
        NavDestinationItem(
            route = StenoRoutes.ROUTE_DRAFT,
            labelRes = R.string.nav_draft,
            selectedIcon = Icons.Filled.EditNote,
            unselectedIcon = Icons.Outlined.EditNote,
            testTag = "nav_draft"
        ),
        NavDestinationItem(
            route = StenoRoutes.ROUTE_CORPUS,
            labelRes = R.string.nav_corpus,
            selectedIcon = Icons.Filled.MenuBook,
            unselectedIcon = Icons.Outlined.MenuBook,
            testTag = "nav_corpus"
        )
    )

    fun navigateToTab(route: String) {
        if (route != currentRoute) interstitialController?.onTabSelected()
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surface,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                StenoTopBar(
                    targetWpm = uiState.prefs.targetWpm,
                    onCycleWpm = {
                        val presets = listOf(80, 100, 120, 140)
                        val idx = presets.indexOf(uiState.prefs.targetWpm)
                        val next = presets[(idx + 1) % presets.size]
                        viewModel.setTargetWpm(next)
                    },
                    onVoiceDictateClick = { launchVoiceDictation() },
                    onProfileClick = { viewModel.setProfileSheetVisible(true) }
                )
            },
            bottomBar = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(onClick = { showSupportDialog = true }) {
                        Text("Support the Developer ❤️")
                    }
                    AdMobBanner(enabled = adsReady)
                    if (!useRail) {
                        NavigationBar(
                            containerColor = extendedColors.surfaceContainerLowest,
                            tonalElevation = 2.dp
                        ) {
                            navItems.forEach { item ->
                                val selected = currentRoute == item.route
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateToTab(item.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = stringResource(item.labelRes)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(item.labelRes),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.secondary,
                                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag(item.testTag)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(
                        containerColor = extendedColors.surfaceContainerLow
                    ) {
                        navItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = stringResource(item.labelRes)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(item.labelRes),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.secondary,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = StenoRoutes.ROUTE_DICTATE
                    ) {
                        composable(StenoRoutes.ROUTE_DICTATE) {
                            DictateScreen(
                                uiState = uiState,
                                onModeSelected = viewModel::setDictationMode,
                                onTabSelected = viewModel::setActiveTab,
                                onHighlightClicked = viewModel::openPassageTooltip,
                                onDismissTooltip = viewModel::dismissPassageTooltip,
                                onPronounceTerm = viewModel::pronounceTermOnly,
                                onExpandTooltipInspector = viewModel::expandTooltipToInspector,
                                onStemClicked = viewModel::pronounceStem,
                                onKaraokeToggle = viewModel::setKaraokeSync,
                                onTtsPlayPause = viewModel::toggleTtsPlayPause,
                                onTtsRestart = viewModel::restartTts,
                                onWpmStep = { delta ->
                                    viewModel.setTargetWpm(uiState.prefs.targetWpm + delta)
                                },
                                onSetTargetWpm = viewModel::setTargetWpm,
                                onSelectAudioSource = viewModel::selectAudioSource,
                                onSelectCadenceProfile = viewModel::selectCadenceProfile,
                                onUnifiedPlayerPlayPause = viewModel::toggleUnifiedPlayerPlayPause,
                                onSeekSeconds = viewModel::seekDictationBySeconds,
                                onTogglePitchPreserve = viewModel::togglePitchPreserve,
                                onToggleFiveSecondCadenceTest = viewModel::toggleFiveSecondCadenceTest,
                                onToggleQuarterMinuteChime = viewModel::setQuarterMinuteChime,
                                onToggleAmbientMasking = viewModel::setCourtroomAmbientMasking,
                                onToggleSpeedProgression = viewModel::setSpeedProgressionLadder,
                                onGenerateDictationExercise = viewModel::generateAndStartDictationExercise,
                                onExportPdf = viewModel::exportActivePassagePdf,
                                onOpenCustomEditor = { navigateToTab(StenoRoutes.ROUTE_DRAFT) },
                                onPickAudioClick = {
                                    audioPickerLauncher.launch(arrayOf("audio/*"))
                                },
                                onSynthesizeAudioClick = viewModel::synthesizeJudgmentToCourtAudio,
                                onCourtAudioPlayPause = viewModel::toggleCourtAudioPlayPause,
                                onCourtAudioSeek = viewModel::seekCourtAudio,
                                onCourtAudioSpeed = viewModel::setCourtAudioSpeed,
                                onBookmarkToggle = { viewModel.toggleBookmark(uiState.activePassage) },
                                onOpenBookmarksSheet = { viewModel.setBookmarksSheetVisible(true) }
                            )
                        }

                        composable(StenoRoutes.ROUTE_SYMBOLS) {
                            SymbolsScreen(
                                uiState = uiState,
                                onSelectGroup = viewModel::setSelectedSymbolGroup,
                                onSearchQueryChange = viewModel::setSymbolSearchQuery,
                                onInspectSymbol = viewModel::inspectSpec,
                                onPronounceTerm = viewModel::pronounceTermOnly,
                                onSelectSubMode = viewModel::setSymbolsSubMode,
                                onJsonQueryChange = viewModel::setJsonLookupQuery,
                                onJsonDomainFilterChange = viewModel::setJsonDomainFilter,
                                onAddJsonLexiconEntry = viewModel::addJsonLexiconEntry,
                                onDeleteJsonLexiconEntry = viewModel::deleteJsonLexiconEntry,
                                onResetJsonLexiconSeed = viewModel::resetJsonLexiconToSeed
                            )
                        }

                        composable(StenoRoutes.ROUTE_DRAFT) {
                            DraftScreen(
                                uiState = uiState,
                                onCitationChange = viewModel::updateCustomCitation,
                                onCourtChange = viewModel::updateCustomCourt,
                                onDraftTextChange = viewModel::updateCustomText,
                                onSelectTemplate = viewModel::applyDraftTemplate,
                                onVoiceDictateClick = { launchVoiceDictation() },
                                onApplyAndDictate = {
                                    viewModel.applyCustomDraftToStudio(startSpeaking = true)
                                    navigateToTab(StenoRoutes.ROUTE_DICTATE)
                                },
                                onApplyOnly = {
                                    viewModel.applyCustomDraftToStudio(startSpeaking = false)
                                    navigateToTab(StenoRoutes.ROUTE_DICTATE)
                                }
                            )
                        }

                        composable(StenoRoutes.ROUTE_CORPUS) {
                            CorpusScreen(
                                uiState = uiState,
                                onFilterChange = viewModel::setCorpusFilter,
                                onSearchQueryChange = viewModel::setCorpusSearchQuery,
                                onSelectCase = { selectedCase ->
                                    viewModel.selectCorpusCase(selectedCase)
                                    navigateToTab(StenoRoutes.ROUTE_DICTATE)
                                },
                                onBookmarkToggle = viewModel::toggleBookmark,
                                onFetchDawnFeed = viewModel::fetchDawnFeed
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text("Support the Developer ❤️") },
            text = {
                Column {
                    Text("You can support the development of Pitman Stenography through Easypaisa.")
                    Text("Easypaisa number: $EASYPAISA_SUPPORT_NUMBER")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    clipboardManager.setText(AnnotatedString(EASYPAISA_SUPPORT_NUMBER))
                    showSupportDialog = false
                }) { Text("Copy number") }
            },
            dismissButton = {
                TextButton(onClick = { showSupportDialog = false }) { Text("Close") }
            }
        )
    }

    // Modal Bottom Sheets
    uiState.inspectedSpec?.let { spec ->
        OutlineInspectorSheet(
            spec = spec,
            onPronounce = { viewModel.pronounceStem(spec) },
            onDismiss = viewModel::dismissInspector
        )
    }

    if (uiState.showProfileSheet) {
        ProfileCalibrationSheet(
            prefs = uiState.prefs,
            onWpmChange = viewModel::setTargetWpm,
            onPitchChange = viewModel::setTtsPitch,
            onThemeAppearanceChange = viewModel::setThemeAppearance,
            onHighContrastChange = viewModel::setHighContrastMode,
            onDismiss = { viewModel.setProfileSheetVisible(false) }
        )
    }

    if (uiState.showBookmarksSheet) {
        BookmarkedPassagesSheet(
            bookmarks = uiState.bookmarkedPassages,
            activePassageId = uiState.activePassage.id,
            targetWpm = uiState.prefs.targetWpm,
            onSelectPassage = viewModel::selectCorpusCase,
            onRemoveBookmark = viewModel::removeBookmark,
            onBrowseCorpus = { navigateToTab(StenoRoutes.ROUTE_CORPUS) },
            onDismiss = { viewModel.setBookmarksSheetVisible(false) }
        )
    }
    }
}
