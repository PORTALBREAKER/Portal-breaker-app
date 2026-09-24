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
    fun testDatabaseSaveAndRead() {
        runBlocking {
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
    }

    @Test
    fun testViewModelWithExistingChapter() {
        runBlocking {
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

            val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
            controller.create().start().resume()
            val activity = controller.get()
            assertNotNull(activity)
            controller.destroy()
        }
    }

    @Test
    fun testSaveChapterThroughViewModelAndReopen() {
        runBlocking {
            val app = ApplicationProvider.getApplicationContext<Application>()
            val vm1 = NovelViewModel(app)
            vm1.saveChapter(
                existingId = null,
                chapterNumber = 2,
                title = "Fractured Dimensions",
                volume = "Volume 1",
                synopsis = "The veil tears apart.",
                content = "The portal pulsed with crimson lightning as Author Arun watched.",
                authorNote = "Chapter 2 release"
            )

            // Allow coroutine to complete insertion
            kotlinx.coroutines.delay(100)

            // Simulate closing the app and reopening
            val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
            controller.create().start().resume()
            val activity = controller.get()
            assertNotNull(activity)
            controller.destroy()
        }
    }

    @Test
    fun testAuthorPasscodeUnlockAndPersistence() {
        runBlocking {
            val app = ApplicationProvider.getApplicationContext<Application>()
            val vm = NovelViewModel(app)
            
            // Verify passcode 6767 succeeds
            val verified = vm.verifySecretKey("6767")
            org.junit.Assert.assertTrue("Author passcode 6767 should be verified", verified)
            org.junit.Assert.assertTrue("Author mode should be active", vm.isAuthorModeActive.value)

            // Re-create ViewModel to simulate closing and relaunching app
            val vmRestarted = NovelViewModel(app)
            org.junit.Assert.assertTrue(
                "Author mode should persist across restarts",
                vmRestarted.isAuthorModeActive.value
            )

            // Test exiting author mode
            vmRestarted.exitAuthorMode()
            org.junit.Assert.assertFalse(
                "Author mode should be disabled after exit",
                vmRestarted.isAuthorModeActive.value
            )
        }
    }

    @Test
    fun testAuthorEmailLoginDirectly() {
        runBlocking {
            val app = ApplicationProvider.getApplicationContext<Application>()
            val vm = NovelViewModel(app)

            // Sign in with Author Arun's email
            var signInSuccess = false
            vm.signInWithEmail("divakaryased123@gmail.com", "6767") { ok, _ ->
                signInSuccess = ok
            }
            kotlinx.coroutines.delay(100)

            org.junit.Assert.assertTrue("Author email sign-in should succeed", signInSuccess)
            org.junit.Assert.assertTrue("Author mode should activate upon author login", vm.isAuthorModeActive.value)
        }
    }
}



