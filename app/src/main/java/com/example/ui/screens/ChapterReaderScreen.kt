package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChapterEntity
import com.example.data.local.ReadingSettingsEntity
import com.example.ui.components.availableThemes
import com.example.ui.theme.PortalCyan
import com.example.ui.theme.PortalGold
import com.example.ui.theme.PortalPurple
import com.example.ui.theme.PortalSky
import com.example.ui.theme.ReaderThemes
import com.example.ui.theme.VoidBorder
import com.example.ui.theme.VoidCard
import com.example.ui.theme.VoidDark
import com.example.ui.theme.VoidSurface
import com.example.ui.theme.glassmorphic
import com.example.ui.theme.iosGlassHud
import com.example.ui.theme.iosReaderGlassSurface
import com.example.ui.theme.neomorphic

@Composable
fun ChapterReaderScreen(
    chapter: ChapterEntity,
    hasNextChapter: Boolean,
    hasPreviousChapter: Boolean,
    settings: ReadingSettingsEntity,
    onBack: () -> Unit,
    onNextChapter: () -> Unit,
    onPreviousChapter: () -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onUpdateProgressWithScroll: (Long, Float, Int, Int) -> Unit,
    onQuickChangeFontSize: (Float) -> Unit,
    onOpenSettings: () -> Unit
) {
    val theme = remember(settings.themeId) {
        availableThemes.firstOrNull { it.id == settings.themeId } ?: availableThemes[0]
    }

    val isLight = remember(theme.id) {
        theme.id == "SOLAR_LIGHT" || theme.id == "SEPIA_PARCHMENT"
    }

    val fontFamily = remember(settings.fontFamilyType) {
        when (settings.fontFamilyType) {
            "SERIF" -> FontFamily.Serif
            "MONOSPACE" -> FontFamily.Monospace
            else -> FontFamily.Default
        }
    }

    var showControls by remember { mutableStateOf(true) }
    // Initialize list state to remembered scroll position if available, keyed per chapter
    val listState = key(chapter.id) {
        rememberLazyListState(
            initialFirstVisibleItemIndex = chapter.scrollPosition,
            initialFirstVisibleItemScrollOffset = chapter.scrollOffset
        )
    }

    // Accurately calculate reading progress based on visible narrative content
    val progress by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf chapter.readProgress

            val totalItems = layoutInfo.totalItemsCount
            if (totalItems <= 1) return@derivedStateOf 0f

            // If user scrolled to or past the end-of-chapter section
            if (visibleItems.any { it.index >= totalItems - 2 }) {
                return@derivedStateOf 1.0f
            }

            // Find narrative content card (item index 2)
            val contentItem = visibleItems.firstOrNull { it.index == 2 }
            if (contentItem != null) {
                val viewportHeight = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset).coerceAtLeast(1)
                val scrollableHeight = (contentItem.size - viewportHeight).coerceAtLeast(1).toFloat()
                val scrolledPx = (-contentItem.offset).coerceAtLeast(0).toFloat()
                (scrolledPx / scrollableHeight).coerceIn(0f, 1f)
            } else if (visibleItems.first().index > 2) {
                1.0f
            } else {
                0.0f
            }
        }
    }

    // Debounced scroll observer for optimal performance on low-end and high-end devices
    LaunchedEffect(chapter.id) {
        snapshotFlow {
            Triple(progress, listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }
        .collectLatest { (prog, index, offset) ->
            delay(400L) // Wait until active scrolling pauses before disk/cloud writes
            if (prog > 0.01f || index > 0) {
                onUpdateProgressWithScroll(chapter.id, prog, index, offset)
            }
        }
    }

    // Immediately persist scroll position when reader leaves or navigates away
    DisposableEffect(chapter.id) {
        onDispose {
            val p = progress
            val idx = listState.firstVisibleItemIndex
            val off = listState.firstVisibleItemScrollOffset
            if (p > 0.01f || idx > 0) {
                onUpdateProgressWithScroll(chapter.id, p, idx, off)
            }
        }
    }

    // Split content into clean paragraphs
    val paragraphs = remember(chapter.content) {
        chapter.content.split(Regex("\n\n+")).map { it.trim() }.filter { it.isNotBlank() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.bgColor)
            .drawBehind {
                val w = size.width
                val h = size.height

                // Atmospheric luminous gradient aura (top-right)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            theme.accentColor.copy(alpha = if (isLight) 0.09f else 0.16f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.85f, h * 0.15f),
                        radius = w * 0.85f
                    )
                )

                // Atmospheric luminous gradient pool (bottom-left)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            (if (isLight) theme.accentColor else PortalPurple).copy(alpha = if (isLight) 0.05f else 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.15f, h * 0.70f),
                        radius = w * 0.75f
                    )
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
    ) {
        // Centered responsive Reader Content container
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp)
                    .padding(horizontal = 18.dp)
                    .testTag("reader_content_list")
            ) {
                // Header Top clearance for floating glass HUD
                item {
                    Spacer(modifier = Modifier.height(96.dp))
                }

                // 1. Chapter Title Hero Glass Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .iosReaderGlassSurface(
                                shape = RoundedCornerShape(22.dp),
                                backgroundColor = if (isLight) Color.White.copy(alpha = 0.92f) else VoidCard.copy(alpha = 0.65f),
                                accentColor = theme.accentColor,
                                isLight = isLight
                            )
                            .padding(22.dp)
                    ) {
                        Column {
                            // Volume Tag Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(theme.accentColor.copy(alpha = if (isLight) 0.14f else 0.20f))
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.linearGradient(
                                            listOf(
                                                theme.accentColor.copy(alpha = 0.6f),
                                                theme.accentColor.copy(alpha = 0.1f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = chapter.volume.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = theme.accentColor,
                                        letterSpacing = 1.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Chapter Title
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = fontFamily,
                                    color = theme.textColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.2.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Metadata pills (word count, reading duration)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background((if (isLight) Color(0xFFE2E8F0) else VoidSurface).copy(alpha = 0.6f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${chapter.wordCount} words",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = theme.textColor.copy(alpha = 0.75f)
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background((if (isLight) Color(0xFFE2E8F0) else VoidSurface).copy(alpha = 0.6f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "~${(chapter.wordCount / 200).coerceAtLeast(1)} min read",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = theme.accentColor,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Specular gradient glowing divider
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                Color.Transparent,
                                                theme.accentColor.copy(alpha = 0.6f),
                                                theme.accentColor.copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. Main Narrative Glass Slate
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .iosReaderGlassSurface(
                                shape = RoundedCornerShape(24.dp),
                                backgroundColor = if (isLight) Color.White.copy(alpha = 0.90f) else VoidCard.copy(alpha = 0.55f),
                                accentColor = theme.accentColor,
                                isLight = isLight
                            )
                            .padding(horizontal = 22.dp, vertical = 26.dp)
                    ) {
                        Column {
                            if (paragraphs.isEmpty()) {
                                Text(
                                    text = chapter.content,
                                    fontSize = settings.fontSizeSp.sp,
                                    lineHeight = (settings.fontSizeSp * settings.lineSpacingMultiplier).sp,
                                    fontFamily = fontFamily,
                                    color = theme.textColor,
                                    textAlign = if (settings.isJustified) TextAlign.Justify else TextAlign.Start
                                )
                            } else {
                                paragraphs.forEachIndexed { index, paragraphText ->
                                    val displayText = if (settings.paragraphIndent) {
                                        "    $paragraphText"
                                    } else {
                                        paragraphText
                                    }

                                    Text(
                                        text = displayText,
                                        fontSize = settings.fontSizeSp.sp,
                                        lineHeight = (settings.fontSizeSp * settings.lineSpacingMultiplier).sp,
                                        fontFamily = fontFamily,
                                        color = theme.textColor,
                                        textAlign = if (settings.isJustified) TextAlign.Justify else TextAlign.Start,
                                        modifier = Modifier.padding(bottom = (settings.fontSizeSp * 0.85f).dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Author Note Section (if inscribed)
                if (chapter.authorNote.isNotBlank()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .iosReaderGlassSurface(
                                    shape = RoundedCornerShape(20.dp),
                                    backgroundColor = if (isLight) Color(0xFFF8FAFC) else VoidCard.copy(alpha = 0.65f),
                                    accentColor = theme.accentColor,
                                    isLight = isLight
                                )
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(theme.accentColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FormatQuote,
                                            contentDescription = null,
                                            tint = theme.accentColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "AUTHOR NOTE • ARUN",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = theme.accentColor,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = chapter.authorNote,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = theme.textColor.copy(alpha = 0.9f),
                                        fontFamily = fontFamily,
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // 4. Chapter End Navigation & Seal
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .iosReaderGlassSurface(
                                shape = RoundedCornerShape(22.dp),
                                backgroundColor = if (isLight) Color.White.copy(alpha = 0.88f) else VoidCard.copy(alpha = 0.60f),
                                accentColor = theme.accentColor,
                                isLight = isLight
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "— END OF CHAPTER —",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = theme.accentColor,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${(progress * 100).toInt()}% of chapter completed",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = theme.textColor.copy(alpha = 0.6f)
                                )
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                if (hasPreviousChapter) {
                                    OutlinedButton(
                                        onClick = onPreviousChapter,
                                        shape = RoundedCornerShape(14.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            theme.accentColor.copy(alpha = 0.45f)
                                        ),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = theme.textColor
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Previous")
                                    }
                                }
                                if (hasNextChapter) {
                                    Button(
                                        onClick = onNextChapter,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = theme.accentColor,
                                            contentColor = if (isLight) Color.White else VoidDark
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text("Next Chapter", fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }
        }

        // TOP VIEWPORT GRADIENT OVERLAY (Dissolves scrolling text seamlessly under top HUD)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(84.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            theme.bgColor.copy(alpha = 0.95f),
                            theme.bgColor.copy(alpha = 0.60f),
                            Color.Transparent
                        )
                    )
                )
        )

        // BOTTOM VIEWPORT GRADIENT OVERLAY (Dissolves scrolling text seamlessly above bottom HUD)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            theme.bgColor.copy(alpha = 0.60f),
                            theme.bgColor.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        // TOP IMMERSIVE READING HUD (Auto-hide on tap)
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .iosGlassHud(
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                        containerColor = if (isLight) Color.White else VoidDark,
                        accentColor = theme.accentColor,
                        isLight = isLight
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                val isCompact = maxWidth < 380.dp

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(if (isCompact) 36.dp else 44.dp).testTag("reader_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = theme.textColor,
                                modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = chapter.title,
                                style = (if (isCompact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleSmall).copy(
                                    color = theme.textColor,
                                    fontWeight = FontWeight.Bold
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${(progress * 100).toInt()}% completed",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = theme.accentColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = if (isCompact) 9.sp else 11.sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { onToggleBookmark(chapter.id) },
                                modifier = Modifier.size(if (isCompact) 34.dp else 40.dp).testTag("reader_bookmark_button")
                            ) {
                                Icon(
                                    imageVector = if (chapter.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (chapter.isBookmarked) PortalGold else theme.textColor,
                                    modifier = Modifier.size(if (isCompact) 18.dp else 22.dp)
                                )
                            }
                            IconButton(
                                onClick = { onToggleFavorite(chapter.id) },
                                modifier = Modifier.size(if (isCompact) 34.dp else 40.dp).testTag("reader_favorite_button")
                            ) {
                                Icon(
                                    imageVector = if (chapter.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (chapter.isFavorite) Color(0xFFFF2A55) else theme.textColor,
                                    modifier = Modifier.size(if (isCompact) 18.dp else 22.dp)
                                )
                            }
                            IconButton(
                                onClick = onOpenSettings,
                                modifier = Modifier.size(if (isCompact) 34.dp else 40.dp).testTag("reader_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = "Typography Settings",
                                    tint = theme.textColor,
                                    modifier = Modifier.size(if (isCompact) 18.dp else 22.dp)
                                )
                            }
                        }
                    }

                // Top Reading Progress Bar with rounded caps and glowing accent
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.5.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = theme.accentColor,
                    trackColor = theme.textColor.copy(alpha = 0.12f)
                )
                }
            }
        }

        // BOTTOM QUICK NAVIGATION HUD (Floating Glass Dock)
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                val isCompact = maxWidth < 380.dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .iosGlassHud(
                            shape = RoundedCornerShape(26.dp),
                            containerColor = if (isLight) Color.White else VoidDark,
                            accentColor = theme.accentColor,
                            isLight = isLight
                        )
                        .padding(horizontal = if (isCompact) 10.dp else 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onPreviousChapter,
                            enabled = hasPreviousChapter,
                            modifier = Modifier.size(if (isCompact) 36.dp else 44.dp).testTag("prev_chapter_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Chapter",
                                tint = if (hasPreviousChapter) theme.accentColor else Color.Gray.copy(alpha = 0.35f),
                                modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                            )
                        }

                        // Chapter Badge Pill & Quick Font Size
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onQuickChangeFontSize((settings.fontSizeSp - 1f).coerceAtLeast(13f)) },
                                modifier = Modifier.size(if (isCompact) 28.dp else 32.dp).testTag("quick_decrease_font")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TextDecrease,
                                    contentDescription = "Decrease Font Size",
                                    tint = theme.textColor.copy(alpha = 0.7f),
                                    modifier = Modifier.size(if (isCompact) 16.dp else 18.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(theme.accentColor.copy(alpha = if (isLight) 0.12f else 0.18f))
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.linearGradient(
                                            listOf(
                                                theme.accentColor.copy(alpha = 0.5f),
                                                theme.accentColor.copy(alpha = 0.1f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = if (isCompact) 10.dp else 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (chapter.chapterNumber == 0) "Prologue" else "Ch ${chapter.chapterNumber}",
                                    style = (if (isCompact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleSmall).copy(
                                        color = theme.textColor,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }

                            IconButton(
                                onClick = { onQuickChangeFontSize((settings.fontSizeSp + 1f).coerceAtMost(28f)) },
                                modifier = Modifier.size(if (isCompact) 28.dp else 32.dp).testTag("quick_increase_font")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TextIncrease,
                                    contentDescription = "Increase Font Size",
                                    tint = theme.textColor.copy(alpha = 0.7f),
                                    modifier = Modifier.size(if (isCompact) 16.dp else 18.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = onNextChapter,
                            enabled = hasNextChapter,
                            modifier = Modifier.size(if (isCompact) 36.dp else 44.dp).testTag("next_chapter_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Chapter",
                                tint = if (hasNextChapter) theme.accentColor else Color.Gray.copy(alpha = 0.35f),
                                modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

