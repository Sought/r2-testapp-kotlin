/**
 * Author: Didier HEMERY
 * Trainee @EDRLab
 * File: AudiobookTests.kt
 */

package org.readium.r2.testapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.readium.r2.testapp.library.LibraryActivity
import org.readium.r2.testapp.setup.*
import org.hamcrest.CoreMatchers.`is` as Is


@RunWith(AndroidJUnit4::class)
@LargeTest
class AudiobookTests {
    @get:Rule
    var activityScenarioRule = activityScenarioRule<CatalogActivity>()

    /**
     * Destroy, recreate the books database and allow sdcard access.
     */
    @Before
    @After
    fun cleanPubs() {
        initTestEnv()
        remPubsFromDeviceInternalMemory()
    }

    /**
     * Gets the current running LibraryActivity
     */
    private fun getActivity(): LibraryActivity? {
        var activity: LibraryActivity? = null
        activityScenarioRule.scenario.onActivity {
            activity = it
        }
        return activity
    }

    /**
     * Tests that the play and pause button swap as they should.
     */
    @Test
    fun testPlayPauseSwaps() {
        val pub = getStr(R.string.audiobookTestFile)
        copyPubFromAPKToDeviceInternalMemory(pub)
        addPubToDatabase(pub, getActivity())
        waitFor(1000)
        onView(withText(getStr(R.string.audiobookTestName))).perform(ViewActions.click())

        for (x in 0..9)
            onView(withId(Is(R.id.play_pause))).perform(ViewActions.click())

        onView(withTagValue(Is(getStr(R.string.pauseButton)))).check(matches(isDisplayed()))
    }


}