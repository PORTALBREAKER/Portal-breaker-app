package com.example.data.repository

import android.util.Log
import com.example.data.local.ChapterDao
import com.example.data.local.ChapterEntity
import com.example.data.local.ReadingSettingsEntity
import com.example.data.local.SettingsDao
import com.example.data.remote.FirestoreNovelSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class NovelRepository(
    private val chapterDao: ChapterDao,
    private val settingsDao: SettingsDao,
    private val cloudSync: FirestoreNovelSync
) {
    val allChapters: Flow<List<ChapterEntity>> = chapterDao.getAllChapters()
    val readingSettings: Flow<ReadingSettingsEntity?> = settingsDao.getSettings()
    val lastReadChapter: Flow<ChapterEntity?> = chapterDao.getLastReadChapter()

    fun isCloudAvailable(): Boolean = cloudSync.isCloudAvailable()

    fun startListeningToCloud(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            cloudSync.observeCloudChapters().collect { remoteList ->
                if (remoteList.isNotEmpty()) {
                    for (remote in remoteList) {
                        val existing = chapterDao.getChapterByNumber(remote.chapterNumber)
                        if (existing == null) {
                            // Ingest newly published chapter from cloud
                            val newChapter = ChapterEntity(
                                chapterNumber = remote.chapterNumber,
                                title = remote.title,
                                volume = remote.volume,
                                synopsis = remote.synopsis,
                                content = remote.content,
                                authorNote = remote.authorNote,
                                wordCount = remote.wordCount,
                                releaseDate = remote.releaseDate
                            )
                            chapterDao.insertChapter(newChapter)
                            Log.d("NovelRepository", "Ingested cloud chapter ${remote.chapterNumber} into local database.")
                        } else {
                            // Update content if changed in cloud
                            if (existing.title != remote.title ||
                                existing.content != remote.content ||
                                existing.synopsis != remote.synopsis ||
                                existing.volume != remote.volume ||
                                existing.authorNote != remote.authorNote) {
                                chapterDao.updateChapter(
                                    existing.copy(
                                        title = remote.title,
                                        volume = remote.volume,
                                        synopsis = remote.synopsis,
                                        content = remote.content,
                                        authorNote = remote.authorNote,
                                        wordCount = remote.wordCount
                                    )
                                )
                                Log.d("NovelRepository", "Updated local chapter ${remote.chapterNumber} from cloud.")
                            }
                        }
                    }
                }
            }
        }
    }

    fun getChapter(id: Long): Flow<ChapterEntity?> = chapterDao.getChapterById(id)

    suspend fun ensureDefaultChapters() {
        if (settingsDao.getSettings().firstOrNull() == null) {
            settingsDao.saveSettings(ReadingSettingsEntity())
        }
        val existingChapters = chapterDao.getAllChapters().firstOrNull()
        if (existingChapters.isNullOrEmpty() && com.example.data.local.DefaultChapters.canonChapters.isNotEmpty()) {
            for (chapter in com.example.data.local.DefaultChapters.canonChapters) {
                chapterDao.insertChapter(chapter)
            }
            Log.d("NovelRepository", "Seeded ${com.example.data.local.DefaultChapters.canonChapters.size} canon chapters for Portal Breaker.")
        }
    }

    suspend fun insertChapter(
        chapterNumber: Int,
        title: String,
        volume: String,
        synopsis: String,
        content: String,
        authorNote: String
    ): Long {
        val wordCount = content.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val entity = ChapterEntity(
            chapterNumber = chapterNumber,
            title = title.trim(),
            volume = volume.ifBlank { "Volume 1" },
            synopsis = synopsis.trim(),
            content = content.trim(),
            authorNote = authorNote.trim(),
            wordCount = wordCount,
            releaseDate = System.currentTimeMillis()
        )
        val id = chapterDao.insertChapter(entity)

        // Sync to cloud worldwide
        cloudSync.publishChapterToCloud(entity.copy(id = id))

        return id
    }

    suspend fun updateChapter(chapter: ChapterEntity) {
        val wordCount = chapter.content.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val updated = chapter.copy(wordCount = wordCount)
        chapterDao.updateChapter(updated)

        // Sync to cloud worldwide
        cloudSync.publishChapterToCloud(updated)
    }

    suspend fun deleteChapter(chapter: ChapterEntity) {
        chapterDao.deleteChapter(chapter)
        cloudSync.deleteChapterFromCloud(chapter.chapterNumber)
    }

    suspend fun updateReadingProgress(chapterId: Long, progress: Float) {
        val isRead = progress >= 0.95f
        chapterDao.updateProgress(
            chapterId = chapterId,
            progress = progress,
            isRead = isRead,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun updateReadingProgressWithScroll(
        chapterId: Long,
        progress: Float,
        scrollPosition: Int,
        scrollOffset: Int
    ) {
        val isRead = progress >= 0.95f
        chapterDao.updateProgressWithScroll(
            chapterId = chapterId,
            progress = progress,
            isRead = isRead,
            scrollPosition = scrollPosition,
            scrollOffset = scrollOffset,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun toggleBookmark(chapterId: Long, currentBookmarked: Boolean) {
        chapterDao.toggleBookmark(chapterId, !currentBookmarked)
    }

    suspend fun toggleFavorite(chapterId: Long, currentFavorite: Boolean) {
        chapterDao.toggleFavorite(chapterId, !currentFavorite)
    }

    suspend fun syncProgressToCloud(userId: String, lastChapter: ChapterEntity?): Result<Unit> {
        if (userId.isBlank()) return Result.success(Unit)
        val chapterToSync = lastChapter
            ?: chapterDao.getLastReadChapter().firstOrNull()
            ?: chapterDao.getAllChapters().firstOrNull()?.firstOrNull()

        val all = chapterDao.getAllChapters().firstOrNull() ?: emptyList()
        val bookmarkedNums = all.filter { it.isBookmarked }.map { it.chapterNumber }
        val favoriteNums = all.filter { it.isFavorite }.map { it.chapterNumber }

        val cloudData = com.example.data.remote.UserCloudProgress(
            userId = userId,
            lastChapterId = chapterToSync?.id ?: 0L,
            lastChapterNumber = chapterToSync?.chapterNumber ?: 1,
            chapterTitle = chapterToSync?.title ?: "Prologue",
            readProgress = chapterToSync?.readProgress ?: 0f,
            scrollPosition = chapterToSync?.scrollPosition ?: 0,
            scrollOffset = chapterToSync?.scrollOffset ?: 0,
            bookmarkedChapterNumbers = bookmarkedNums,
            favoriteChapterNumbers = favoriteNums
        )
        return cloudSync.saveUserProgress(cloudData)
    }

    suspend fun restoreProgressFromCloud(userId: String): Result<com.example.data.remote.UserCloudProgress?> {
        val result = cloudSync.loadUserProgress(userId)
        val cloudData = result.getOrNull()
        if (cloudData != null) {
            // Restore bookmarks and favorites
            val all = chapterDao.getAllChapters().firstOrNull() ?: emptyList()
            for (ch in all) {
                if (ch.chapterNumber in cloudData.bookmarkedChapterNumbers && !ch.isBookmarked) {
                    chapterDao.toggleBookmark(ch.id, true)
                }
                if (ch.chapterNumber in cloudData.favoriteChapterNumbers && !ch.isFavorite) {
                    chapterDao.toggleFavorite(ch.id, true)
                }
                if (ch.chapterNumber == cloudData.lastChapterNumber) {
                    chapterDao.updateProgressWithScroll(
                        chapterId = ch.id,
                        progress = cloudData.readProgress,
                        isRead = cloudData.readProgress >= 0.95f,
                        scrollPosition = cloudData.scrollPosition,
                        scrollOffset = cloudData.scrollOffset,
                        timestamp = System.currentTimeMillis()
                    )
                }
            }
        }
        return result
    }

    suspend fun saveSettings(settings: ReadingSettingsEntity) {
        settingsDao.saveSettings(settings)
    }

    suspend fun resetToDefaultCanon() {
        chapterDao.clearAll()
    }
}
