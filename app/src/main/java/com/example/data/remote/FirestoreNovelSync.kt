package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.local.ChapterEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Remote cloud synchronizer powered by Firebase Firestore.
 * Supports:
 * - Realtime updates for all readers worldwide
 * - Offline cache persistence
 * - Graceful fallback when Firebase is not yet initialized or network is unavailable
 */
data class UserCloudProgress(
    val userId: String,
    val lastChapterId: Long = 0,
    val lastChapterNumber: Int = 1,
    val chapterTitle: String = "",
    val readProgress: Float = 0f,
    val scrollPosition: Int = 0,
    val scrollOffset: Int = 0,
    val bookmarkedChapterNumbers: List<Int> = emptyList(),
    val favoriteChapterNumbers: List<Int> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)

class FirestoreNovelSync(private val context: Context) {

    private val tag = "FirestoreNovelSync"
    private val collectionName = "published_chapters"
    private val userProgressCollection = "reader_progress"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseHelper.ensureInitialized(context)
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(tag, "Failed to get FirebaseFirestore instance: ${e.message}")
            null
        }
    }

    /**
     * Checks if cloud sync is currently functional with Firebase credentials
     */
    fun isCloudAvailable(): Boolean = firestore != null

    /**
     * Saves or restores reader cloud progress by user identification (email / code)
     */
    suspend fun saveUserProgress(progress: UserCloudProgress): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firebase is not connected."))
        return try {
            val docId = progress.userId.trim().lowercase().replace(Regex("[^a-z0-9_]"), "_")
            val data = hashMapOf(
                "userId" to progress.userId,
                "lastChapterNumber" to progress.lastChapterNumber,
                "chapterTitle" to progress.chapterTitle,
                "readProgress" to progress.readProgress,
                "scrollPosition" to progress.scrollPosition,
                "scrollOffset" to progress.scrollOffset,
                "bookmarkedChapterNumbers" to progress.bookmarkedChapterNumbers,
                "favoriteChapterNumbers" to progress.favoriteChapterNumbers,
                "lastUpdated" to System.currentTimeMillis()
            )
            db.collection(userProgressCollection).document(docId).set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to save user progress: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun loadUserProgress(userId: String): Result<UserCloudProgress?> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firebase is not connected."))
        return try {
            val docId = userId.trim().lowercase().replace(Regex("[^a-z0-9_]"), "_")
            val doc = db.collection(userProgressCollection).document(docId).get().await()
            if (doc.exists()) {
                val lastNum = doc.getLong("lastChapterNumber")?.toInt() ?: 1
                val title = doc.getString("chapterTitle") ?: ""
                val progress = doc.getDouble("readProgress")?.toFloat() ?: 0f
                val scrollPos = doc.getLong("scrollPosition")?.toInt() ?: 0
                val scrollOff = doc.getLong("scrollOffset")?.toInt() ?: 0
                @Suppress("UNCHECKED_CAST")
                val bookmarks = (doc.get("bookmarkedChapterNumbers") as? List<Long>)?.map { it.toInt() } ?: emptyList()
                @Suppress("UNCHECKED_CAST")
                val favorites = (doc.get("favoriteChapterNumbers") as? List<Long>)?.map { it.toInt() } ?: emptyList()
                val updated = doc.getLong("lastUpdated") ?: System.currentTimeMillis()

                Result.success(
                    UserCloudProgress(
                        userId = userId,
                        lastChapterNumber = lastNum,
                        chapterTitle = title,
                        readProgress = progress,
                        scrollPosition = scrollPos,
                        scrollOffset = scrollOff,
                        bookmarkedChapterNumbers = bookmarks,
                        favoriteChapterNumbers = favorites,
                        lastUpdated = updated
                    )
                )
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to load user progress: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Publishes or updates a chapter in Firestore so all readers across the globe receive it.
     */
    suspend fun publishChapterToCloud(chapter: ChapterEntity): Result<Unit> {
        val db = firestore ?: return Result.failure(
            IllegalStateException("Firebase is not configured yet. Chapter saved locally.")
        )

        return try {
            val docId = "chapter_${chapter.chapterNumber}"
            val data = hashMapOf(
                "chapterNumber" to chapter.chapterNumber,
                "title" to chapter.title,
                "volume" to chapter.volume,
                "synopsis" to chapter.synopsis,
                "content" to chapter.content,
                "authorNote" to chapter.authorNote,
                "wordCount" to chapter.wordCount,
                "releaseDate" to chapter.releaseDate,
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection(collectionName)
                .document(docId)
                .set(data, SetOptions.merge())
                .await()

            Log.d(tag, "Chapter ${chapter.chapterNumber} published to cloud successfully.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to publish chapter to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a chapter from the cloud database
     */
    suspend fun deleteChapterFromCloud(chapterNumber: Int): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firebase is not configured."))
        return try {
            val docId = "chapter_${chapterNumber}"
            db.collection(collectionName).document(docId).delete().await()
            Log.d(tag, "Chapter $chapterNumber deleted from cloud.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to delete chapter from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Listens in realtime for chapters published or modified by the author in the cloud.
     * Emits the latest list of chapters whenever changes occur worldwide.
     */
    fun observeCloudChapters(): Flow<List<RemoteChapter>> = callbackFlow {
        val db = firestore
        if (db == null) {
            // Emitting empty list if Firebase is not yet configured
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        var registration: ListenerRegistration? = null
        try {
            registration = db.collection(collectionName)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(tag, "Cloud snapshot listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            try {
                                val number = doc.getLong("chapterNumber")?.toInt() ?: return@mapNotNull null
                                val title = doc.getString("title") ?: ""
                                val volume = doc.getString("volume") ?: "Volume 1"
                                val synopsis = doc.getString("synopsis") ?: ""
                                val content = doc.getString("content") ?: ""
                                val authorNote = doc.getString("authorNote") ?: ""
                                val wordCount = doc.getLong("wordCount")?.toInt() ?: 0
                                val releaseDate = doc.getLong("releaseDate") ?: System.currentTimeMillis()

                                RemoteChapter(
                                    chapterNumber = number,
                                    title = title,
                                    volume = volume,
                                    synopsis = synopsis,
                                    content = content,
                                    authorNote = authorNote,
                                    wordCount = wordCount,
                                    releaseDate = releaseDate
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        trySend(list)
                    }
                }
        } catch (e: Exception) {
            Log.e(tag, "Failed to register snapshot listener: ${e.message}", e)
            trySend(emptyList())
        }

        awaitClose {
            registration?.remove()
        }
    }
}

data class RemoteChapter(
    val chapterNumber: Int,
    val title: String,
    val volume: String,
    val synopsis: String,
    val content: String,
    val authorNote: String,
    val wordCount: Int,
    val releaseDate: Long
)
