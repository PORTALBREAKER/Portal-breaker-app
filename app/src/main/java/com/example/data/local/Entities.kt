package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chapterNumber: Int,
    val title: String,
    val volume: String = "Volume 1: Awakening of the Void",
    val synopsis: String = "",
    val content: String,
    val authorNote: String = "",
    val wordCount: Int = 0,
    val releaseDate: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val readProgress: Float = 0f,
    val isBookmarked: Boolean = false,
    val isFavorite: Boolean = false,
    val scrollPosition: Int = 0,
    val scrollOffset: Int = 0,
    val lastReadTimestamp: Long = 0L
)

@Entity(tableName = "reading_settings")
data class ReadingSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val themeId: String = "MIDNIGHT_NEBULA", // DARK_VOID, MIDNIGHT_NEBULA, SEPIA_PARCHMENT, CYBER_MINT, SOLAR_LIGHT
    val fontSizeSp: Float = 17f,
    val lineSpacingMultiplier: Float = 1.6f,
    val fontFamilyType: String = "SERIF", // SERIF, SANS, MONOSPACE
    val isJustified: Boolean = true,
    val paragraphIndent: Boolean = true,
    val keepScreenOn: Boolean = true
)
