package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChapterEntity
import com.example.data.local.ReadingSettingsEntity
import com.example.data.remote.AuthUserState
import com.example.ui.AppTab
import com.example.ui.LibrarySubTab
import com.example.ui.components.NovelCoverCard
import com.example.ui.components.PortalThunderboltLogo
import com.example.ui.theme.PortalCosmicBackground
import com.example.ui.theme.PortalCyan
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalPurple
import com.example.ui.theme.PortalSky
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark
import com.example.ui.theme.VoidSurface
import com.example.ui.theme.VoidSurfaceVariant
import com.example.ui.theme.glassmorphic
import com.example.ui.theme.neomorphic

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NovelOverviewScreen(
    chapters: List<ChapterEntity>,
    allChapters: List<ChapterEntity>,
    lastReadChapter: ChapterEntity?,
    readingSettings: ReadingSettingsEntity,
    isAuthorMode: Boolean,
    selectedTab: AppTab,
    librarySubTab: LibrarySubTab,
    currentUserId: String,
    authUserState: AuthUserState = AuthUserState(),
    isCloudSyncing: Boolean,
    cloudStatusMessage: String?,
    searchQuery: String,
    filterBookmarked: Boolean,
    sortAscending: Boolean,
    onSelectTab: (AppTab) -> Unit,
    onSelectLibrarySubTab: (LibrarySubTab) -> Unit,
    onOpenChapter: (Long) -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    onSecretTrickTriggered: () -> Unit,
    onOpenInscribeNew: () -> Unit,
    onOpenEditChapter: (ChapterEntity) -> Unit,
    onDeleteChapter: (ChapterEntity) -> Unit,
    onExitAuthorMode: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleBookmarkFilter: () -> Unit,
    onToggleSortOrder: () -> Unit,
    onLoginAndSync: (String) -> Unit,
    onSignUp: (String, String, (Boolean, String?) -> Unit) -> Unit = { _, _, cb -> cb(false, null) },
    onSignIn: (String, String, (Boolean, String?) -> Unit) -> Unit = { _, _, cb -> cb(false, null) },
    onSendPasswordReset: (String, (Boolean, String?) -> Unit) -> Unit = { _, cb -> cb(false, null) },
    onSignOutAuth: () -> Unit = {},
    onSyncNow: () -> Unit = {},
    onLogoutCloud: () -> Unit,
    onClearCloudStatus: () -> Unit
) {
    var chapterToDelete by remember { mutableStateOf<ChapterEntity?>(null) }
    var showCloudDialog by remember { mutableStateOf(false) }
    var secretTapCount by remember { mutableIntStateOf(0) }
    var lastTapTimestamp by remember { mutableLongStateOf(0L) }

    PortalCosmicBackground(
        modifier = Modifier.fillMaxSize(),
        isDark = readingSettings.themeId != "SOLAR_LIGHT"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Emblem & Title (Secret 4-tap trigger on the P-Thunderbolt logo only)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    PortalThunderboltLogo(
                        size = 40.dp,
                        onClick = {
                            val now = System.currentTimeMillis()
                            if (now - lastTapTimestamp < 1500L) {
                                secretTapCount++
                            } else {
                                secretTapCount = 1
                            }
                            lastTapTimestamp = now

                            if (secretTapCount >= 4) {
                                secretTapCount = 0
                                onSecretTrickTriggered()
                            }
                        },
                        modifier = Modifier.testTag("secret_portal_thunderbolt_logo")
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PORTAL BREAKER",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Official Webnovel Edition",
                            style = MaterialTheme.typography.labelSmall.copy(color = PortalSky)
                        )
                    }
                }

                // Action Icons (Cloud Sync + Reading Settings)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Cloud Sync Account Button
                    IconButton(
                        onClick = { showCloudDialog = true },
                        modifier = Modifier.testTag("open_cloud_sync_button")
                    ) {
                        if (isCloudSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = PortalCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (currentUserId.isNotBlank()) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                contentDescription = "Cloud Sync",
                                tint = if (currentUserId.isNotBlank()) PortalCyan else Color.Gray
                            )
                        }
                    }

                    // Reading Settings Button
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("open_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TextFields,
                            contentDescription = "Reading Settings",
                            tint = PortalCyan
                        )
                    }
                }
            }

            // AUTHOR MODE ACTIVE BANNER (Only visible after secret trick is unlocked!)
            AnimatedVisibility(
                visible = isAuthorMode,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .glassmorphic(
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = PortalPurple.copy(alpha = 0.25f),
                            borderColor = PortalCyan,
                            secondaryBorderColor = PortalPurple
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = PortalCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "AUTHOR SANCTUARY ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PortalCyan,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Author Arun",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(PortalCyan.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.CloudDone,
                                                contentDescription = null,
                                                tint = PortalCyan,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Worldwide Cloud Sync",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = PortalCyan,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Secret Inscribe Chapter Button
                            Button(
                                onClick = onOpenInscribeNew,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PortalCyan,
                                    contentColor = VoidDark
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("author_inscribe_chapter_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Inscribe Chapter",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            // Exit Author Mode Button
                            OutlinedButton(
                                onClick = onExitAuthorMode,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Exit", color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Cloud Status Notification Banner
            AnimatedVisibility(
                visible = cloudStatusMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                cloudStatusMessage?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(VoidSurface)
                            .border(1.dp, PortalCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = null,
                                    tint = PortalCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = msg,
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = onClearCloudStatus,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Main Content Area by Tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    AppTab.HOME -> {
                        HomeTabContent(
                            chapters = chapters,
                            allChapters = allChapters,
                            lastReadChapter = lastReadChapter,
                            onOpenChapter = onOpenChapter,
                            onSwitchToChapters = { onSelectTab(AppTab.CHAPTERS) }
                        )
                    }

                    AppTab.CHAPTERS -> {
                        ChaptersTabContent(
                            chapters = chapters,
                            isAuthorMode = isAuthorMode,
                            searchQuery = searchQuery,
                            filterBookmarked = filterBookmarked,
                            sortAscending = sortAscending,
                            onOpenChapter = onOpenChapter,
                            onToggleBookmark = onToggleBookmark,
                            onToggleFavorite = onToggleFavorite,
                            onOpenEditChapter = onOpenEditChapter,
                            onRequestDeleteChapter = { chapterToDelete = it },
                            onSearchQueryChange = onSearchQueryChange,
                            onToggleBookmarkFilter = onToggleBookmarkFilter,
                            onToggleSortOrder = onToggleSortOrder
                        )
                    }

                    AppTab.LIBRARY -> {
                        LibraryTabContent(
                            allChapters = allChapters,
                            currentSubTab = librarySubTab,
                            currentUserId = currentUserId,
                            onSelectSubTab = onSelectLibrarySubTab,
                            onOpenChapter = onOpenChapter,
                            onToggleBookmark = onToggleBookmark,
                            onToggleFavorite = onToggleFavorite,
                            onOpenCloudSync = { showCloudDialog = true }
                        )
                    }
                }
            }

            // Bottom Navigation Bar
            NavigationBar(
                containerColor = VoidCard,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .testTag("app_bottom_navigation")
            ) {
                NavigationBarItem(
                    selected = selectedTab == AppTab.HOME,
                    onClick = { onSelectTab(AppTab.HOME) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VoidDark,
                        selectedTextColor = PortalCyan,
                        indicatorColor = PortalCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                NavigationBarItem(
                    selected = selectedTab == AppTab.CHAPTERS,
                    onClick = { onSelectTab(AppTab.CHAPTERS) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Chapters"
                        )
                    },
                    label = { Text("Chapters", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VoidDark,
                        selectedTextColor = PortalCyan,
                        indicatorColor = PortalCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_tab_chapters")
                )

                NavigationBarItem(
                    selected = selectedTab == AppTab.LIBRARY,
                    onClick = { onSelectTab(AppTab.LIBRARY) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CollectionsBookmark,
                            contentDescription = "Library"
                        )
                    },
                    label = { Text("Library", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = VoidDark,
                        selectedTextColor = PortalCyan,
                        indicatorColor = PortalCyan,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_tab_library")
                )
            }
        }
    }

    // Cloud Sync & Firebase Auth Dialog
    if (showCloudDialog) {
        CloudSyncDialog(
            authUserState = authUserState,
            currentUserId = currentUserId,
            isSyncing = isCloudSyncing,
            onDismiss = { showCloudDialog = false },
            onSignUp = { email, pass, cb ->
                onSignUp(email, pass, cb)
            },
            onSignIn = { email, pass, cb ->
                onSignIn(email, pass, cb)
            },
            onSendPasswordReset = { email, cb ->
                onSendPasswordReset(email, cb)
            },
            onSyncNow = onSyncNow,
            onLogout = {
                onSignOutAuth()
                showCloudDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    chapterToDelete?.let { chapter ->
        AlertDialog(
            onDismissRequest = { chapterToDelete = null },
            title = { Text("Delete Chapter", color = Color.White) },
            text = {
                Text(
                    "Are you sure you want to delete \"${chapter.title}\"? This action cannot be undone.",
                    color = Color.LightGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteChapter(chapter)
                        chapterToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { chapterToDelete = null }) {
                    Text("Cancel", color = Color.LightGray)
                }
            },
            containerColor = VoidDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun HomeTabContent(
    chapters: List<ChapterEntity>,
    allChapters: List<ChapterEntity>,
    lastReadChapter: ChapterEntity?,
    onOpenChapter: (Long) -> Unit,
    onSwitchToChapters: () -> Unit
) {
    val totalWords = remember(allChapters) { allChapters.sumOf { it.wordCount } }
    val completedCount = remember(allChapters) { allChapters.count { it.isRead } }
    val bookmarkedCount = remember(allChapters) { allChapters.count { it.isBookmarked } }
    val favoritesCount = remember(allChapters) { allChapters.count { it.isFavorite } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Hero Novel Showcase Card
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassmorphic(
                        shape = RoundedCornerShape(24.dp),
                        backgroundColor = VoidSurface.copy(alpha = 0.85f),
                        borderColor = PortalCyan.copy(alpha = 0.35f)
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 3:4 Procedural Cover Card
                    NovelCoverCard(
                        modifier = Modifier
                            .width(115.dp)
                            .height(165.dp)
                    )

                    // Novel Details & Metadata
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PORTAL BREAKER",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Author: Arun",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = PortalSky,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Genre Tags
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Dark Fantasy", "LitRPG", "Sci-Fi").forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .glassmorphic(
                                            shape = RoundedCornerShape(6.dp),
                                            backgroundColor = PortalPurple.copy(alpha = 0.2f),
                                            borderColor = PortalPurple.copy(alpha = 0.5f),
                                            borderWidth = 0.5.dp
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.LightGray,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Original Webnovel Publication by Author Arun",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFA0AEC0),
                                letterSpacing = 0.3.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Novel Stats Pill
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${allChapters.size} Chapters",
                                style = MaterialTheme.typography.labelSmall.copy(color = PortalCyan)
                            )
                            Text(text = "•", color = Color.DarkGray)
                            Text(
                                text = "Ongoing",
                                style = MaterialTheme.typography.labelSmall.copy(color = PortalGold)
                            )
                            Text(text = "•", color = Color.DarkGray)
                            Text(
                                text = "${totalWords / 1000}k Words",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.LightGray)
                            )
                        }
                    }
                }
            }
        }

        // Continue Reading Quick Jump Card (or Start Reading Prologue)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            val targetChapter = lastReadChapter ?: allChapters.firstOrNull()
            if (targetChapter != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassmorphic(
                            shape = RoundedCornerShape(18.dp),
                            backgroundColor = VoidCard.copy(alpha = 0.9f),
                            borderColor = PortalCyan.copy(alpha = 0.4f)
                        )
                        .clickable { onOpenChapter(targetChapter.id) }
                        .padding(16.dp)
                        .testTag("continue_reading_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = PortalCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lastReadChapter != null) "CONTINUE READING" else "START READING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = PortalCyan,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = targetChapter.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { targetChapter.readProgress },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = PortalCyan,
                                trackColor = VoidBorder
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(PortalCyan)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Resume Reading",
                                tint = VoidDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // Reader Stats Grid
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Completed Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(VoidCard)
                        .border(1.dp, VoidBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Completed", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$completedCount / ${allChapters.size}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = PortalCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Bookmarks Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(VoidCard)
                        .border(1.dp, VoidBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Bookmarked", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$bookmarkedCount",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = PortalGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Favorites Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(VoidCard)
                        .border(1.dp, VoidBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "Favorites", style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$favoritesCount",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFFFF2A55),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Quick Jump to Chapters
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onSwitchToChapters,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("browse_all_chapters_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PortalCyan,
                    contentColor = VoidDark
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Browse Table of Contents", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun ChaptersTabContent(
    chapters: List<ChapterEntity>,
    isAuthorMode: Boolean,
    searchQuery: String,
    filterBookmarked: Boolean,
    sortAscending: Boolean,
    onOpenChapter: (Long) -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onOpenEditChapter: (ChapterEntity) -> Unit,
    onRequestDeleteChapter: (ChapterEntity) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleBookmarkFilter: () -> Unit,
    onToggleSortOrder: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Section Title + Search & Filters
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TABLE OF CONTENTS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PortalSky,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Bookmark Filter Toggle
                    IconButton(
                        onClick = onToggleBookmarkFilter,
                        modifier = Modifier.size(36.dp).testTag("filter_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (filterBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Filter Bookmarks",
                            tint = if (filterBookmarked) PortalGold else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Sort Order Toggle
                    IconButton(
                        onClick = onToggleSortOrder,
                        modifier = Modifier.size(36.dp).testTag("sort_order_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort Order",
                            tint = if (sortAscending) PortalCyan else PortalPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Chapter Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search chapters or keywords...", color = Color.Gray, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PortalCyan,
                    unfocusedBorderColor = VoidBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = VoidCard.copy(alpha = 0.5f),
                    unfocusedContainerColor = VoidCard.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("chapter_search_input")
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Chapters List
        if (chapters.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotBlank()) {
                            "No chapters match \"$searchQuery\""
                        } else if (isAuthorMode) {
                            "No chapters yet. Tap 'Inscribe Chapter' above to write and publish your first chapter!"
                        } else {
                            "No chapters published yet. Stay tuned for Author Arun's upcoming chapters!"
                        },
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            items(chapters, key = { it.id }) { chapter ->
                ChapterListItemCard(
                    chapter = chapter,
                    isAuthorMode = isAuthorMode,
                    onClick = { onOpenChapter(chapter.id) },
                    onToggleBookmark = { onToggleBookmark(chapter.id) },
                    onToggleFavorite = { onToggleFavorite(chapter.id) },
                    onEdit = { onOpenEditChapter(chapter) },
                    onDelete = { onRequestDeleteChapter(chapter) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun LibraryTabContent(
    allChapters: List<ChapterEntity>,
    currentSubTab: LibrarySubTab,
    currentUserId: String,
    onSelectSubTab: (LibrarySubTab) -> Unit,
    onOpenChapter: (Long) -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onOpenCloudSync: () -> Unit
) {
    val filteredChapters = remember(allChapters, currentSubTab) {
        when (currentSubTab) {
            LibrarySubTab.ALL -> allChapters.sortedBy { it.chapterNumber }
            LibrarySubTab.BOOKMARKS -> allChapters.filter { it.isBookmarked }.sortedBy { it.chapterNumber }
            LibrarySubTab.FAVORITES -> allChapters.filter { it.isFavorite }.sortedBy { it.chapterNumber }
            LibrarySubTab.HISTORY -> allChapters.filter { it.readProgress > 0f || it.isRead }.sortedByDescending { it.readProgress }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Cloud Status Banner in Library
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(VoidCard)
                .border(1.dp, VoidBorder, RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenCloudSync)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("library_cloud_banner")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (currentUserId.isNotBlank()) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = if (currentUserId.isNotBlank()) PortalCyan else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (currentUserId.isNotBlank()) "Cloud Sync Active" else "Enable Cross-Device Cloud Sync",
                            style = MaterialTheme.typography.titleSmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = if (currentUserId.isNotBlank()) "Account: $currentUserId" else "Tap to backup reading progress & bookmarks",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 11.sp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = PortalCyan,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sub-tabs row
        TabRow(
            selectedTabIndex = currentSubTab.ordinal,
            containerColor = Color.Transparent,
            contentColor = PortalCyan,
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[currentSubTab.ordinal]),
                    color = PortalCyan
                )
            }
        ) {
            LibrarySubTab.values().forEach { subTab ->
                val title = when (subTab) {
                    LibrarySubTab.ALL -> "All"
                    LibrarySubTab.BOOKMARKS -> "Bookmarks"
                    LibrarySubTab.FAVORITES -> "Favorites"
                    LibrarySubTab.HISTORY -> "History"
                }
                val isSelected = currentSubTab == subTab
                Tab(
                    selected = isSelected,
                    onClick = { onSelectSubTab(subTab) },
                    text = {
                        Text(
                            text = title,
                            color = if (isSelected) PortalCyan else Color.Gray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.testTag("library_subtab_${subTab.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chapters in this shelf
        if (filteredChapters.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = when (currentSubTab) {
                            LibrarySubTab.BOOKMARKS -> Icons.Default.BookmarkBorder
                            LibrarySubTab.FAVORITES -> Icons.Default.FavoriteBorder
                            LibrarySubTab.HISTORY -> Icons.Default.History
                            LibrarySubTab.ALL -> Icons.Default.CollectionsBookmark
                        },
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (currentSubTab) {
                            LibrarySubTab.BOOKMARKS -> "No bookmarked chapters yet.\nTap the bookmark icon in any chapter to save it here."
                            LibrarySubTab.FAVORITES -> "No favorite chapters yet.\nTap the heart icon to save your beloved moments."
                            LibrarySubTab.HISTORY -> "No reading history recorded.\nDive into Chapter 1 to start tracking your journey!"
                            LibrarySubTab.ALL -> "Your library is empty."
                        },
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredChapters, key = { it.id }) { chapter ->
                    ChapterListItemCard(
                        chapter = chapter,
                        isAuthorMode = false,
                        onClick = { onOpenChapter(chapter.id) },
                        onToggleBookmark = { onToggleBookmark(chapter.id) },
                        onToggleFavorite = { onToggleFavorite(chapter.id) },
                        onEdit = {},
                        onDelete = {}
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }
        }
    }
}

@Composable
fun ChapterListItemCard(
    chapter: ChapterEntity,
    isAuthorMode: Boolean,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val estimatedMinutes = (chapter.wordCount / 200).coerceAtLeast(1)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .neomorphic(
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp,
                surfaceColor = VoidCard
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("chapter_card_${chapter.chapterNumber}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chapter Number Crest
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VoidSurfaceVariant)
                    .border(
                        1.dp,
                        if (chapter.isRead) PortalCyan.copy(alpha = 0.4f) else VoidBorder,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (chapter.isRead) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Read",
                        tint = PortalCyan,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (chapter.chapterNumber == 0) "P" else "#${chapter.chapterNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = PortalCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Chapter Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (chapter.synopsis.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = chapter.synopsis,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.LightGray,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${chapter.wordCount} words",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                    Text(text = "•", color = Color.DarkGray)
                    Text(
                        text = "~$estimatedMinutes min",
                        style = MaterialTheme.typography.labelSmall.copy(color = PortalSky)
                    )
                    if (chapter.readProgress > 0f && !chapter.isRead) {
                        Text(text = "•", color = Color.DarkGray)
                        Text(
                            text = "${(chapter.readProgress * 100).toInt()}% read",
                            style = MaterialTheme.typography.labelSmall.copy(color = PortalGold)
                        )
                    }
                }
            }

            // Normal Reader: Favorite & Bookmark Icons. Author Mode: Edit/Delete buttons!
            if (isAuthorMode) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp).testTag("edit_chapter_${chapter.chapterNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Chapter",
                            tint = PortalCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp).testTag("delete_chapter_${chapter.chapterNumber}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Chapter",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(34.dp).testTag("favorite_chapter_${chapter.chapterNumber}")
                    ) {
                        Icon(
                            imageVector = if (chapter.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (chapter.isFavorite) Color(0xFFFF2A55) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(34.dp).testTag("bookmark_chapter_${chapter.chapterNumber}")
                    ) {
                        Icon(
                            imageVector = if (chapter.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (chapter.isBookmarked) PortalGold else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CloudSyncDialog(
    authUserState: AuthUserState,
    currentUserId: String,
    isSyncing: Boolean,
    onDismiss: () -> Unit,
    onSignUp: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onSignIn: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onSendPasswordReset: (String, (Boolean, String?) -> Unit) -> Unit,
    onSyncNow: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedAuthMode by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Create Account
    var emailInput by remember { mutableStateOf(authUserState.email.ifBlank { "" }) }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    val isLoggedIn = authUserState.isLoggedIn || currentUserId.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = PortalCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isLoggedIn) "Cloud Account & Sync" else "Firebase Cloud Account",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                if (isLoggedIn) {
                    // Logged-in Account Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .neomorphic(
                                shape = RoundedCornerShape(14.dp),
                                surfaceColor = VoidCard
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PortalCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = PortalCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = authUserState.email.ifBlank { currentUserId },
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Cloud Synchronized",
                                        color = PortalSky,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (authUserState.uid.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "UID: ${authUserState.uid}",
                                    color = Color.Gray,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Your reading progress, scroll offsets, bookmarks, and favorite chapters are synced across devices via Firestore Cloud Storage. Progress is safely saved even if you reinstall the app.",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )

                    if (infoMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = infoMessage!!, color = PortalCyan, fontSize = 12.sp)
                    }
                } else {
                    // Tab Selector: Sign In vs Create Account
                    TabRow(
                        selectedTabIndex = selectedAuthMode,
                        containerColor = VoidCard,
                        contentColor = PortalCyan,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedAuthMode]),
                                color = PortalCyan
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = selectedAuthMode == 0,
                            onClick = {
                                selectedAuthMode = 0
                                errorMessage = null
                                infoMessage = null
                            },
                            text = { Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_auth_sign_in")
                        )
                        Tab(
                            selected = selectedAuthMode == 1,
                            onClick = {
                                selectedAuthMode = 1
                                errorMessage = null
                                infoMessage = null
                            },
                            text = { Text("Create Account", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                            modifier = Modifier.testTag("tab_auth_create_account")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (selectedAuthMode == 0)
                            "Sign in with your email to restore your reading progress and bookmarks across devices."
                        else
                            "Create an account to save your reading progress to the cloud even if you reinstall the app.",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Email field
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            errorMessage = null
                        },
                        label = { Text("Email Address", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = PortalCyan, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PortalCyan,
                            unfocusedBorderColor = VoidBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password field
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorMessage = null
                        },
                        label = { Text("Password", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = PortalCyan, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PortalCyan,
                            unfocusedBorderColor = VoidBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
                    )

                    // Confirm Password (if Create Account)
                    if (selectedAuthMode == 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = confirmPasswordInput,
                            onValueChange = {
                                confirmPasswordInput = it
                                errorMessage = null
                            },
                            label = { Text("Confirm Password", color = Color.Gray, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = PortalCyan, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PortalCyan,
                                unfocusedBorderColor = VoidBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_confirm_password_input")
                        )
                    }

                    // Forgot Password (if Sign In)
                    if (selectedAuthMode == 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    if (emailInput.isBlank()) {
                                        errorMessage = "Enter your email to receive a password reset link."
                                    } else {
                                        onSendPasswordReset(emailInput) { ok, err ->
                                            if (ok) {
                                                infoMessage = "Password reset instructions sent to $emailInput."
                                                errorMessage = null
                                            } else {
                                                errorMessage = err ?: "Failed to send reset link."
                                            }
                                        }
                                    }
                                }
                            ) {
                                Text("Forgot password?", color = PortalSky, fontSize = 11.sp)
                            }
                        }
                    }

                    // Error Message
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage!!,
                            color = Color(0xFFEF4444),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Info Message
                    if (infoMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = infoMessage!!,
                            color = PortalCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (isLoggedIn) {
                Button(
                    onClick = onSyncNow,
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PortalCyan,
                        contentColor = VoidDark
                    ),
                    modifier = Modifier.testTag("cloud_sync_now_button")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = VoidDark, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                    } else {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text("Sync Now", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        if (emailInput.isBlank()) {
                            errorMessage = "Please enter your email."
                            return@Button
                        }
                        if (passwordInput.isBlank()) {
                            errorMessage = "Please enter your password."
                            return@Button
                        }
                        if (selectedAuthMode == 1) {
                            if (passwordInput.length < 6) {
                                errorMessage = "Password must be at least 6 characters."
                                return@Button
                            }
                            if (passwordInput != confirmPasswordInput) {
                                errorMessage = "Passwords do not match."
                                return@Button
                            }
                            onSignUp(emailInput, passwordInput) { success, err ->
                                if (success) {
                                    infoMessage = "Account created successfully!"
                                    errorMessage = null
                                } else {
                                    errorMessage = err ?: "Account creation failed."
                                }
                            }
                        } else {
                            onSignIn(emailInput, passwordInput) { success, err ->
                                if (success) {
                                    infoMessage = "Signed in successfully!"
                                    errorMessage = null
                                } else {
                                    errorMessage = err ?: "Sign in failed."
                                }
                            }
                        }
                    },
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PortalCyan,
                        contentColor = VoidDark
                    ),
                    modifier = Modifier.testTag("auth_submit_button")
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = VoidDark, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = if (selectedAuthMode == 0) "Sign In" else "Create Account",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            Row {
                if (isLoggedIn) {
                    TextButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("auth_sign_out_button")
                    ) {
                        Text("Sign Out", color = Color(0xFFEF4444))
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Close", color = Color.LightGray)
                }
            }
        },
        containerColor = VoidDark,
        shape = RoundedCornerShape(20.dp)
    )
}

