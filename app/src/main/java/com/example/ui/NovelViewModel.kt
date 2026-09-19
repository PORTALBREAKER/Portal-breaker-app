package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChapterEntity
import com.example.data.local.PortalBreakerDatabase
import com.example.data.local.ReadingSettingsEntity
import com.example.data.remote.AuthUserState
import com.example.data.remote.FirebaseAuthService
import com.example.data.remote.FirestoreNovelSync
import com.example.data.repository.NovelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab {
    HOME,
    CHAPTERS,
    LIBRARY
}

enum class LibrarySubTab {
    ALL,
    BOOKMARKS,
    FAVORITES,
    HISTORY
}

class NovelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NovelRepository
    private val authService = FirebaseAuthService(application)

    // Firebase Auth user state
    val authUserState: StateFlow<AuthUserState> = authService.currentUserState

    // Cloud & User Sync
    private val prefs = application.getSharedPreferences("portal_breaker_user", android.content.Context.MODE_PRIVATE)
    private val _currentUserId = MutableStateFlow(
        authService.currentUserState.value.email.ifBlank {
            authService.currentUserState.value.uid.ifBlank {
                prefs.getString("user_id", "") ?: ""
            }
        }
    )
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _cloudStatusMessage = MutableStateFlow<String?>(null)
    val cloudStatusMessage: StateFlow<String?> = _cloudStatusMessage.asStateFlow()

    // Primary Navigation Tab: Home, Chapters, Library
    private val _selectedTab = MutableStateFlow(AppTab.HOME)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    // Library Sub-tab
    private val _librarySubTab = MutableStateFlow(LibrarySubTab.ALL)
    val librarySubTab: StateFlow<LibrarySubTab> = _librarySubTab.asStateFlow()

    init {
        val database = PortalBreakerDatabase.getDatabase(application, viewModelScope)
        val cloudSync = FirestoreNovelSync(application)
        repository = NovelRepository(database.chapterDao(), database.settingsDao(), cloudSync)
        
        viewModelScope.launch {
            repository.ensureDefaultChapters()
        }
        
        // Start background realtime sync for global readers
        repository.startListeningToCloud(viewModelScope)

        // Monitor Firebase Auth session and automatically sync
        viewModelScope.launch {
            authService.currentUserState.collect { user ->
                if (user.isLoggedIn) {
                    val uid = user.uid.ifBlank { user.email }
                    _currentUserId.value = user.email.ifBlank { user.uid }
                    prefs.edit().putString("user_id", _currentUserId.value).apply()
                    syncWithUser(uid)

                    // Auto-activate Author Mode if logged into the Author's verified Gmail account
                    if (user.email.trim().equals(AUTHOR_EMAIL, ignoreCase = true)) {
                        _isAuthorModeActive.value = true
                    }
                } else {
                    _currentUserId.value = ""
                }
            }
        }
    }

    val isCloudAvailable: Boolean
        get() = repository.isCloudAvailable()

    val allChapters: StateFlow<List<ChapterEntity>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingSettings: StateFlow<ReadingSettingsEntity> = repository.readingSettings
        .combine(MutableStateFlow(ReadingSettingsEntity())) { settings, default ->
            settings ?: default
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReadingSettingsEntity())

    val lastReadChapter: StateFlow<ChapterEntity?> = repository.lastReadChapter
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation & UI States
    private val _selectedChapterId = MutableStateFlow<Long?>(null)
    val selectedChapterId: StateFlow<Long?> = _selectedChapterId.asStateFlow()

    // Secret Author Mode State (Default FALSE so normal readers only see reading app)
    private val _isAuthorModeActive = MutableStateFlow(false)
    val isAuthorModeActive: StateFlow<Boolean> = _isAuthorModeActive.asStateFlow()

    private val _showSecretDialog = MutableStateFlow(false)
    val showSecretDialog: StateFlow<Boolean> = _showSecretDialog.asStateFlow()

    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    private val _showInscribeSheet = MutableStateFlow(false)
    val showInscribeSheet: StateFlow<Boolean> = _showInscribeSheet.asStateFlow()

    private val _editingChapter = MutableStateFlow<ChapterEntity?>(null)
    val editingChapter: StateFlow<ChapterEntity?> = _editingChapter.asStateFlow()

    // Filter & Sort
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterBookmarked = MutableStateFlow(false)
    val filterBookmarked: StateFlow<Boolean> = _filterBookmarked.asStateFlow()

    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending.asStateFlow()

    // Filtered Chapters list
    val displayedChapters: StateFlow<List<ChapterEntity>> = combine(
        allChapters,
        _searchQuery,
        _filterBookmarked,
        _sortAscending
    ) { chapters, query, bookmarkedOnly, asc ->
        var list = chapters
        if (query.isNotBlank()) {
            list = list.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.synopsis.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true) ||
                        "Chapter ${it.chapterNumber}".contains(query, ignoreCase = true)
            }
        }
        if (bookmarkedOnly) {
            list = list.filter { it.isBookmarked }
        }
        if (asc) {
            list.sortedBy { it.chapterNumber }
        } else {
            list.sortedByDescending { it.chapterNumber }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun openChapter(chapterId: Long) {
        _selectedChapterId.value = chapterId
    }

    fun closeChapter() {
        _selectedChapterId.value = null
    }

    fun openNextChapter() {
        val currentId = _selectedChapterId.value ?: return
        val list = allChapters.value.sortedBy { it.chapterNumber }
        val currentIndex = list.indexOfFirst { it.id == currentId }
        if (currentIndex != -1 && currentIndex < list.size - 1) {
            _selectedChapterId.value = list[currentIndex + 1].id
        }
    }

    fun openPreviousChapter() {
        val currentId = _selectedChapterId.value ?: return
        val list = allChapters.value.sortedBy { it.chapterNumber }
        val currentIndex = list.indexOfFirst { it.id == currentId }
        if (currentIndex > 0) {
            _selectedChapterId.value = list[currentIndex - 1].id
        }
    }

    fun updateReadingProgress(chapterId: Long, progress: Float) {
        viewModelScope.launch {
            repository.updateReadingProgress(chapterId, progress)
        }
    }

    fun updateReadingProgressWithScroll(chapterId: Long, progress: Float, scrollPosition: Int, scrollOffset: Int) {
        viewModelScope.launch {
            repository.updateReadingProgressWithScroll(chapterId, progress, scrollPosition, scrollOffset)
            val uid = _currentUserId.value
            if (uid.isNotBlank()) {
                val ch = allChapters.value.firstOrNull { it.id == chapterId }
                if (ch != null) {
                    repository.syncProgressToCloud(
                        userId = uid,
                        lastChapter = ch.copy(readProgress = progress, scrollPosition = scrollPosition, scrollOffset = scrollOffset)
                    )
                }
            }
        }
    }

    fun toggleBookmark(chapterId: Long) {
        val chapter = allChapters.value.firstOrNull { it.id == chapterId } ?: return
        viewModelScope.launch {
            repository.toggleBookmark(chapterId, chapter.isBookmarked)
            val uid = _currentUserId.value
            if (uid.isNotBlank()) {
                repository.syncProgressToCloud(uid, lastReadChapter.value)
            }
        }
    }

    fun toggleFavorite(chapterId: Long) {
        val chapter = allChapters.value.firstOrNull { it.id == chapterId } ?: return
        viewModelScope.launch {
            repository.toggleFavorite(chapterId, chapter.isFavorite)
            val uid = _currentUserId.value
            if (uid.isNotBlank()) {
                repository.syncProgressToCloud(uid, lastReadChapter.value)
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun selectLibrarySubTab(subTab: LibrarySubTab) {
        _librarySubTab.value = subTab
    }

    fun signUpWithEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        _isCloudSyncing.value = true
        _cloudStatusMessage.value = "Creating Firebase account..."
        viewModelScope.launch {
            val res = authService.signUpWithEmail(email, pass)
            _isCloudSyncing.value = false
            if (res.isSuccess) {
                val state = res.getOrThrow()
                _cloudStatusMessage.value = "Account created for ${state.email}! Cloud sync active."
                syncWithUser(state.uid.ifBlank { state.email })
                onResult(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Account creation failed."
                _cloudStatusMessage.value = err
                onResult(false, err)
            }
        }
    }

    fun signInWithEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        _isCloudSyncing.value = true
        _cloudStatusMessage.value = "Signing in..."
        viewModelScope.launch {
            val res = authService.signInWithEmail(email, pass)
            _isCloudSyncing.value = false
            if (res.isSuccess) {
                val state = res.getOrThrow()
                _cloudStatusMessage.value = "Welcome back, ${state.displayName}!"
                syncWithUser(state.uid.ifBlank { state.email })
                onResult(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Sign in failed."
                _cloudStatusMessage.value = err
                onResult(false, err)
            }
        }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val res = authService.sendPasswordReset(email)
            if (res.isSuccess) {
                _cloudStatusMessage.value = "Password reset instructions sent to $email."
                onResult(true, null)
            } else {
                val err = res.exceptionOrNull()?.message ?: "Could not send reset email."
                _cloudStatusMessage.value = err
                onResult(false, err)
            }
        }
    }

    fun signOutCloud() {
        authService.signOut()
        prefs.edit().remove("user_id").apply()
        _currentUserId.value = ""
        _cloudStatusMessage.value = "Signed out. Progress saved locally."
    }

    fun syncNow() {
        val user = authUserState.value
        val uid = if (user.isLoggedIn) user.uid.ifBlank { user.email } else _currentUserId.value
        if (uid.isNotBlank()) {
            syncWithUser(uid)
        }
    }

    fun loginAndSyncCloudProgress(userId: String) {
        val cleanId = userId.trim()
        if (cleanId.isBlank()) return
        prefs.edit().putString("user_id", cleanId).apply()
        _currentUserId.value = cleanId
        syncWithUser(cleanId)
    }

    fun logoutCloud() {
        signOutCloud()
    }

    private fun syncWithUser(userId: String) {
        _isCloudSyncing.value = true
        _cloudStatusMessage.value = "Synchronizing with cloud..."
        viewModelScope.launch {
            val result = repository.restoreProgressFromCloud(userId)
            _isCloudSyncing.value = false
            if (result.isSuccess) {
                val data = result.getOrNull()
                if (data != null) {
                    _cloudStatusMessage.value = "Synced! Last read: Chapter ${data.lastChapterNumber}"
                } else {
                    // Newly linked user; push current local progress to cloud
                    repository.syncProgressToCloud(userId, lastReadChapter.value)
                    _cloudStatusMessage.value = "Progress backed up to cloud!"
                }
            } else {
                // If restore had no document, push local
                repository.syncProgressToCloud(userId, lastReadChapter.value)
                _cloudStatusMessage.value = "Cloud sync up to date."
            }
        }
    }

    fun clearCloudStatusMessage() {
        _cloudStatusMessage.value = null
    }

    companion object {
        const val AUTHOR_EMAIL = "divakaryased123@gmail.com"
        const val AUTHOR_PASSCODE = "6767"
    }

    // Secret trick triggers
    fun onSecretTrickTriggered() {
        _showSecretDialog.value = true
    }

    fun dismissSecretDialog() {
        _showSecretDialog.value = false
    }

    fun verifySecretKey(enteredKey: String): Boolean {
        val trimmed = enteredKey.trim()
        val currentEmail = authUserState.value.email.trim()

        // Author access granted if:
        // 1. Logged-in Firebase account matches the author's Gmail: divakaryased123@gmail.com
        // 2. Or the entered key matches author's Gmail: divakaryased123@gmail.com
        // 3. Or the author passcode is entered while logged into the author Gmail, or standard passcode "6767"
        val isAuthorEmailMatch = currentEmail.equals(AUTHOR_EMAIL, ignoreCase = true) ||
                trimmed.equals(AUTHOR_EMAIL, ignoreCase = true)
        val isPasscodeMatch = trimmed == AUTHOR_PASSCODE

        val isCorrect = isAuthorEmailMatch || isPasscodeMatch
        if (isCorrect) {
            _isAuthorModeActive.value = true
            _showSecretDialog.value = false
        }
        return isCorrect
    }

    fun exitAuthorMode() {
        _isAuthorModeActive.value = false
    }

    fun openInscribeNewChapter() {
        _editingChapter.value = null
        _showInscribeSheet.value = true
    }

    fun openEditChapter(chapter: ChapterEntity) {
        _editingChapter.value = chapter
        _showInscribeSheet.value = true
    }

    fun closeInscribeSheet() {
        _showInscribeSheet.value = false
        _editingChapter.value = null
    }

    fun saveChapter(
        existingId: Long?,
        chapterNumber: Int,
        title: String,
        volume: String,
        synopsis: String,
        content: String,
        authorNote: String
    ) {
        viewModelScope.launch {
            if (existingId == null || existingId == 0L) {
                repository.insertChapter(
                    chapterNumber = chapterNumber,
                    title = title,
                    volume = volume,
                    synopsis = synopsis,
                    content = content,
                    authorNote = authorNote
                )
            } else {
                val existing = allChapters.value.firstOrNull { it.id == existingId }
                if (existing != null) {
                    repository.updateChapter(
                        existing.copy(
                            chapterNumber = chapterNumber,
                            title = title,
                            volume = volume,
                            synopsis = synopsis,
                            content = content,
                            authorNote = authorNote
                        )
                    )
                }
            }
            _showInscribeSheet.value = false
            _editingChapter.value = null
        }
    }

    fun deleteChapter(chapter: ChapterEntity) {
        viewModelScope.launch {
            repository.deleteChapter(chapter)
            if (_selectedChapterId.value == chapter.id) {
                _selectedChapterId.value = null
            }
        }
    }

    fun resetToCanonChapters() {
        viewModelScope.launch {
            repository.resetToDefaultCanon()
        }
    }

    fun toggleSettingsSheet(show: Boolean) {
        _showSettingsSheet.value = show
    }

    fun updateSettings(settings: ReadingSettingsEntity) {
        viewModelScope.launch {
            repository.saveSettings(settings)
        }
    }

    fun quickChangeFontSize(newSizeSp: Float) {
        val current = readingSettings.value
        viewModelScope.launch {
            repository.saveSettings(current.copy(fontSizeSp = newSizeSp))
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFilterBookmarked() {
        _filterBookmarked.value = !_filterBookmarked.value
    }

    fun toggleSortOrder() {
        _sortAscending.value = !_sortAscending.value
    }
}
