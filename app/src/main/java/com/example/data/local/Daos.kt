package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters ORDER BY chapterNumber ASC")
    fun getAllChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE id = :id LIMIT 1")
    fun getChapterById(id: Long): Flow<ChapterEntity?>

    @Query("SELECT * FROM chapters WHERE chapterNumber = :chapterNumber LIMIT 1")
    suspend fun getChapterByNumber(chapterNumber: Int): ChapterEntity?

    @Query("SELECT * FROM chapters ORDER BY lastReadTimestamp DESC LIMIT 1")
    fun getLastReadChapter(): Flow<ChapterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Query("UPDATE chapters SET readProgress = :progress, isRead = :isRead, scrollPosition = :scrollPosition, scrollOffset = :scrollOffset, lastReadTimestamp = :timestamp WHERE id = :chapterId")
    suspend fun updateProgressWithScroll(chapterId: Long, progress: Float, isRead: Boolean, scrollPosition: Int, scrollOffset: Int, timestamp: Long)

    @Query("UPDATE chapters SET readProgress = :progress, isRead = :isRead, lastReadTimestamp = :timestamp WHERE id = :chapterId")
    suspend fun updateProgress(chapterId: Long, progress: Float, isRead: Boolean, timestamp: Long)

    @Query("UPDATE chapters SET isBookmarked = :isBookmarked WHERE id = :chapterId")
    suspend fun toggleBookmark(chapterId: Long, isBookmarked: Boolean)

    @Query("UPDATE chapters SET isFavorite = :isFavorite WHERE id = :chapterId")
    suspend fun toggleFavorite(chapterId: Long, isFavorite: Boolean)

    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)

    @Query("DELETE FROM chapters WHERE id = :chapterId")
    suspend fun deleteChapterById(chapterId: Long)

    @Query("DELETE FROM chapters")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun getChapterCount(): Int
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM reading_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<ReadingSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: ReadingSettingsEntity)
}
