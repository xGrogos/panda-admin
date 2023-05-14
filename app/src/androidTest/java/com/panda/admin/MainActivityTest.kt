package com.panda.admin

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Test
    fun testGoogleSignIn() {
        // Launch the MainActivity
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // Simulate clicking on the "Google Icon" button
        Espresso.onView(ViewMatchers.withId(R.id.buttonSignIn)).perform(ViewActions.click())
        // Pause the current thread for 3 second
        Thread.sleep(3000)

        // Close the activity
        activityScenario.close()
    }
}