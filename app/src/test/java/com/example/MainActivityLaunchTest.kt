package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.NovelViewModel
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.Robolectric

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityLaunchTest {

    @Test
    fun `viewModel and activity can initialize without crashing`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = NovelViewModel(app)
        assertNotNull(viewModel)

        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }
}
