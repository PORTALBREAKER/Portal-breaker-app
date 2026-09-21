package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.room.Room
import com.example.data.local.ChapterEntity
import com.example.data.local.PortalBreakerDatabase
import com.example.ui.NovelViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityLaunchTest {

    @Test
    fun testDatabaseSaveAndRead() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val db = Room.databaseBuilder(
            context,
            PortalBreakerDatabase::class.java,
            "test_portal_breaker_vault"
        ).allowMainThreadQueries().build()

        val chapter = ChapterEntity(
            chapterNumber = 1,
            title = "Awakening of the Void",
            volume = "Volume 1",
            synopsis = "A mysterious portal opens.",
            content = "Dark lightning crackled through the sky...",
            authorNote = "Enjoy the chapter!",
            wordCount = 500,
            releaseDate = System.currentTimeMillis()
        )
        val id = db.chapterDao().insertChapter(chapter)
        val all = db.chapterDao().getAllChapters().first()
        assertEquals(1, all.size)
        assertEquals(1, all[0].chapterNumber)

        val lastRead = db.chapterDao().getLastReadChapter().first()
        assertNotNull(lastRead)
        db.close()
    }

    @Test
    fun testViewModelWithExistingChapter() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        // Insert a chapter into the actual vault database used by ViewModel
        val db = Room.databaseBuilder(
            app,
            PortalBreakerDatabase::class.java,
            "portal_breaker_arun_vault"
        ).allowMainThreadQueries().build()

        db.chapterDao().insertChapter(
            ChapterEntity(
                chapterNumber = 1,
                title = "Awakening of the Void",
                volume = "Volume 1",
                synopsis = "A mysterious portal opens.",
                content = "Dark lightning crackled through the sky...",
                authorNote = "Enjoy the chapter!",
                wordCount = 500,
                releaseDate = System.currentTimeMillis()
            )
        )
        db.close()

        val vm = NovelViewModel(app)
        assertNotNull(vm)
    }
}



