package com.example

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ChapterEntity
import com.example.data.local.PortalBreakerDatabase
import com.example.ui.NovelViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NovelOverviewRenderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRenderAppWithPersistedChapter() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = PortalBreakerDatabase.getDatabase(app, kotlinx.coroutines.GlobalScope)

        runBlocking {
            db.chapterDao().insertChapter(
                ChapterEntity(
                    chapterNumber = 1,
                    title = "Test Chapter 1",
                    volume = "Volume 1",
                    synopsis = "Synopsis here",
                    content = "Content of the test chapter.",
                    authorNote = "Author note"
                )
            )
        }

        val viewModel = NovelViewModel(app)

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            PortalBreakerApp(viewModel = viewModel)
        }

        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.onRoot().assertExists()
    }

    @Test
    fun testActivityLaunchWithPersistedChapter() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = PortalBreakerDatabase.getDatabase(app, kotlinx.coroutines.GlobalScope)

        runBlocking {
            db.chapterDao().insertChapter(
                ChapterEntity(
                    chapterNumber = 1,
                    title = "Test Chapter 1",
                    volume = "Volume 1",
                    synopsis = "Synopsis here",
                    content = "Content of the test chapter.",
                    authorNote = "Author note"
                )
            )
        }

        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
        controller.create().start().resume()
        val activity = controller.get()
        org.junit.Assert.assertNotNull(activity)
        controller.pause().stop().destroy()
    }

}
