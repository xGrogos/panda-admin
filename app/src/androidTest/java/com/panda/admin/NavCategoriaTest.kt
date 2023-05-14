package com.panda.admin

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class NavCategoriaTest {

    @Test
    fun testCategoriaNavigation() {
        // Launch the HomeActivity
        val activityScenario = ActivityScenario.launch(HomeActivity::class.java)

        // Simulate clicking on the "categoria" item in the bottom navigation view
        onView(withId(R.id.nav_categoria)).perform(click())
        // Pause the current thread for 5 second
        Thread.sleep(5000)

        // Close the activity
        activityScenario.close()
    }
}
