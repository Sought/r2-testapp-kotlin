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

    private fun addTestAudiobook(pub: String) {
        copyPubFromAPKToDeviceInternalMemory(pub)
        addPubToDatabase(pub, getActivity())
        waitFor(1000)

        onView(withText(getStr(R.string.audiobookTestName))).perform(ViewActions.click())
    }
/*
    /**
     * Tests that the play and pause button swap as they should.
     */
    @Test
    fun testPlayPauseSwaps() {
        addTestAudiobook(getStr(R.string.audiobookTestFile))

        for (x in 0..9)
            onView(withId(Is(R.id.play_pause))).perform(ViewActions.click())

        onView(withTagValue(Is(getStr(R.string.pauseButton)))).check(matches(isDisplayed()))
    }

    /**
     * Tests that changing orientation doesn't resume playing if stopped before.
     */
    @Test
    fun testPauseSwapOrientation() {
        addTestAudiobook(getStr(R.string.audiobookTestFile))

        val device = UiDevice.getInstance(getInstrumentation())

        onView(withId(Is(R.id.play_pause))).perform(ViewActions.click())

        device.setOrientationLeft()
        device.setOrientationNatural()

        onView(withTagValue(Is(getStr(R.string.playButton)))).check(matches(isDisplayed()))
    }


    /**
     * Tests that changing orientation doesn't pause the book playback.
     */
    @Test
    fun testPlaySwapOrientation() {
        addTestAudiobook(getStr(R.string.audiobookTestFile))

        val device = UiDevice.getInstance(getInstrumentation())

        device.setOrientationLeft()
        device.setOrientationNatural()

        onView(withTagValue(Is(getStr(R.string.pauseButton)))).check(matches(isDisplayed()))
    }

    /**
     * Tests that a bookmark can be added
     */
    @Test
    fun testBookmarkSimple() {
        addTestAudiobook(getStr(R.string.audiobookTestFile))

        onView(withId(Is(R.id.bookmark))).perform(ViewActions.click())
        onView(withId(Is(R.id.toc))).perform(ViewActions.click())
        onView(withText("BOOKMARKS")).perform(ViewActions.click())
        onView(withText("00 - Dedication")).check(matches(isDisplayed()))
    }*/

    /**
     * Tests that the TOC works for the middle chapter
     */
    @Test
    fun testTOC() {
        addTestAudiobook(getStr(R.string.audiobookTestFile))
        onView(withId(Is(R.id.toc))).perform(ViewActions.click())
        onView(withText("02 - John Bunyan")).perform(ViewActions.click())
        onView(withText("02 - John Bunyan")).check(matches(isDisplayed()))
    }

    /**
     * Tests that the previous chapter arrow works for the middle chapter
     */
    @Test
    fun testPrevChapMiddle() {
        addTestAudiobook(getStr(R.string.audiobookTestFile))
        onView(withId(Is(R.id.toc))).perform(ViewActions.click())
        onView(withText("01 - Mr. Pepys")).perform(ViewActions.click())
        onView(withId(R.id.prev_chapter)).perform(ViewActions.click())
        onView(withText("00 - Dedication")).check(matches(isDisplayed()))
    }

    /**
     * Tests that the next chapter arrow works for the middle chapter
     */
    @Test
    fun testNextChapMiddle() {
        addTestAudiobook(getStr(R.string.audiobookTestFile))
        onView(withId(Is(R.id.toc))).perform(ViewActions.click())
        onView(withText("01 - Mr. Pepys")).perform(ViewActions.click())
        onView(withId(R.id.next_chapter)).perform(ViewActions.click())
        onView(withText("02 - John Bunyan")).check(matches(isDisplayed()))
    }

    /**
     * Tests that the next chapter arrow works for the last chapter
     */
    @Test
    fun testNextChapEnd() {
        addTestAudiobook(getStr(R.string.audiobookTestFile))
        onView(withId(Is(R.id.toc))).perform(ViewActions.click())
        onView(withText("02 - John Bunyan")).perform(ViewActions.click())
        onView(withId(R.id.next_chapter)).perform(ViewActions.click())
        onView(withText("02 - John Bunyan")).check(matches(isDisplayed()))
    }

    /**
     * Tests that the previous chapter arrow works for the first chapter
     */
    @Test
    fun testPrevChapBeginning() {
        addTestAudiobook(getStr(R.string.audiobookTestFile))
        onView(withId(Is(R.id.toc))).perform(ViewActions.click())
        onView(withText("00 - Dedication")).perform(ViewActions.click())
        onView(withId(R.id.prev_chapter)).perform(ViewActions.click())
        onView(withText("00 - Dedication")).check(matches(isDisplayed()))
    }

}
