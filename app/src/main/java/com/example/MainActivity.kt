package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ui.NovelViewModel
import com.example.ui.components.InscribeChapterSheet
import com.example.ui.components.ReadingSettingsSheet
import com.example.ui.components.SecretKeypadDialog
import com.example.ui.screens.ChapterReaderScreen
import com.example.ui.screens.NovelOverviewScreen
import com.example.ui.theme.PortalBreakerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: NovelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val readingSettings by viewModel.readingSettings.collectAsState()

            // Keep screen on if requested in typography settings
            DisposableEffect(readingSettings.keepScreenOn) {
                if (readingSettings.keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                onDispose {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            val isDarkTheme = readingSettings.themeId != "SOLAR_LIGHT"

            PortalBreakerTheme(darkTheme = isDarkTheme) {
                PortalBreakerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PortalBreakerApp(viewModel: NovelViewModel) {
    val chapters by viewModel.displayedChapters.collectAsState()
    val allChaptersList by viewModel.allChapters.collectAsState()
    val lastReadChapter by viewModel.lastReadChapter.collectAsState()
    val readingSettings by viewModel.readingSettings.collectAsState()
    val selectedChapterId by viewModel.selectedChapterId.collectAsState()
    val isAuthorMode by viewModel.isAuthorModeActive.collectAsState()
    val showSecretDialog by viewModel.showSecretDialog.collectAsState()
    val showSettingsSheet by viewModel.showSettingsSheet.collectAsState()
    val showInscribeSheet by viewModel.showInscribeSheet.collectAsState()
    val editingChapter by viewModel.editingChapter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterBookmarked by viewModel.filterBookmarked.collectAsState()
    val sortAscending by viewModel.sortAscending.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val librarySubTab by viewModel.librarySubTab.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val authUserState by viewModel.authUserState.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val cloudStatusMessage by viewModel.cloudStatusMessage.collectAsState()

    val currentChapter = remember(selectedChapterId, allChaptersList) {
        allChaptersList.firstOrNull { it.id == selectedChapterId }
    }

    val hasNextChapter = remember(selectedChapterId, allChaptersList) {
        val sorted = allChaptersList.sortedBy { it.chapterNumber }
        val index = sorted.indexOfFirst { it.id == selectedChapterId }
        index != -1 && index < sorted.size - 1
    }

    val hasPreviousChapter = remember(selectedChapterId, allChaptersList) {
        val sorted = allChaptersList.sortedBy { it.chapterNumber }
        val index = sorted.indexOfFirst { it.id == selectedChapterId }
        index > 0
    }

    // Handle Back Navigation cleanly
    if (selectedChapterId != null) {
        BackHandler {
            viewModel.closeChapter()
        }
    } else if (isAuthorMode) {
        BackHandler {
            viewModel.exitAuthorMode()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (currentChapter != null) {
            ChapterReaderScreen(
                chapter = currentChapter,
                hasNextChapter = hasNextChapter,
                hasPreviousChapter = hasPreviousChapter,
                settings = readingSettings,
                onBack = { viewModel.closeChapter() },
                onNextChapter = { viewModel.openNextChapter() },
                onPreviousChapter = { viewModel.openPreviousChapter() },
                onToggleBookmark = { viewModel.toggleBookmark(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onUpdateProgressWithScroll = { id, progress, pos, offset ->
                    viewModel.updateReadingProgressWithScroll(id, progress, pos, offset)
                },
                onQuickChangeFontSize = { viewModel.quickChangeFontSize(it) },
                onOpenSettings = { viewModel.toggleSettingsSheet(true) }
            )
        } else {
            NovelOverviewScreen(
                chapters = chapters,
                allChapters = allChaptersList,
                lastReadChapter = lastReadChapter,
                readingSettings = readingSettings,
                isAuthorMode = isAuthorMode,
                selectedTab = selectedTab,
                librarySubTab = librarySubTab,
                currentUserId = currentUserId,
                isCloudSyncing = isCloudSyncing,
                cloudStatusMessage = cloudStatusMessage,
                searchQuery = searchQuery,
                filterBookmarked = filterBookmarked,
                sortAscending = sortAscending,
                onSelectTab = { viewModel.selectTab(it) },
                onSelectLibrarySubTab = { viewModel.selectLibrarySubTab(it) },
                onOpenChapter = { viewModel.openChapter(it) },
                onToggleBookmark = { viewModel.toggleBookmark(it) },
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onOpenSettings = { viewModel.toggleSettingsSheet(true) },
                onSecretTrickTriggered = { viewModel.onSecretTrickTriggered() },
                onOpenInscribeNew = { viewModel.openInscribeNewChapter() },
                onOpenEditChapter = { viewModel.openEditChapter(it) },
                onDeleteChapter = { viewModel.deleteChapter(it) },
                onExitAuthorMode = { viewModel.exitAuthorMode() },
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onToggleBookmarkFilter = { viewModel.toggleFilterBookmarked() },
                onToggleSortOrder = { viewModel.toggleSortOrder() },
                onLoginAndSync = { viewModel.loginAndSyncCloudProgress(it) },
                authUserState = authUserState,
                onSignUp = { email, pass, cb -> viewModel.signUpWithEmail(email, pass, cb) },
                onSignIn = { email, pass, cb -> viewModel.signInWithEmail(email, pass, cb) },
                onSendPasswordReset = { email, cb -> viewModel.sendPasswordReset(email, cb) },
                onSignOutAuth = { viewModel.signOutCloud() },
                onSyncNow = { viewModel.syncNow() },
                onLogoutCloud = { viewModel.logoutCloud() },
                onClearCloudStatus = { viewModel.clearCloudStatusMessage() }
            )
        }

        // Secret Creator Dialog
        if (showSecretDialog) {
            SecretKeypadDialog(
                onDismiss = { viewModel.dismissSecretDialog() },
                onSuccess = { /* Automatically handled in verifyKey */ },
                verifyKey = { enteredKey -> viewModel.verifySecretKey(enteredKey) }
            )
        }

        // Reading & Typography Settings Sheet
        if (showSettingsSheet) {
            ReadingSettingsSheet(
                settings = readingSettings,
                onSettingsChanged = { viewModel.updateSettings(it) },
                onDismiss = { viewModel.toggleSettingsSheet(false) }
            )
        }

        // Author Inscribe / Edit Chapter Sheet
        if (showInscribeSheet) {
            val nextNumber = remember(allChaptersList) {
                (allChaptersList.maxOfOrNull { it.chapterNumber } ?: 0) + 1
            }
            InscribeChapterSheet(
                existingChapter = editingChapter,
                nextSuggestedNumber = nextNumber,
                onSave = { id, num, title, vol, syn, body, note ->
                    viewModel.saveChapter(id, num, title, vol, syn, body, note)
                },
                onDismiss = { viewModel.closeInscribeSheet() }
            )
        }
    }
}
