/**
 * Author: Didier HEMERY
 * Trainee @EDRLab
 * File: AddLocalBook.kt
 */

package org.readium.r2.testapp

import android.os.Build
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.filters.LargeTest
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.readium.r2.testapp.setup.clickButtonUiAutomator
import org.readium.r2.testapp.setup.getStr
import org.readium.r2.testapp.setup.initTestEnv


@RunWith(AndroidJUnit4::class)
@LargeTest
class AddLocalBook
{
    @get:Rule var activityScenarioRule = activityScenarioRule<CatalogActivity>()

    /**
     * Destroy, recreate the books database and allow sdcard access.
     */
    @Before
    @After
    fun cleanPubs() {
         initTestEnv()
    }

    /**
     * Once the device's file explorer is open, navigate into the test files' folder.
     */
    private fun goToTestFiles() {
        // TODO: add a variant for each test device used, as the UI changes on different versions.

        when (Build.VERSION.SDK_INT){
            23 -> {
                //ANDROID 6 TABLET
                clickButtonUiAutomator(getStr(R.string.InternalStorage_SDK23))
                clickButtonUiAutomator(getStr(R.string.FilesFolderName))
            }
            else -> throw Exception("Unsupported Android version for this test")
        }

    }

    /**
     * Tests that importing a CBZ works. Resets the imported database, imports the cbz publication
     * then checks that a coverImageView exists. It currently does not check if the cover is the
     * right one. It might confuse the test if multiple imageViews are present (and make it fail
     * even if the CBZ file was added).
     */
    @Test
    fun importLocalCBZPublicationWorks() {
        onView(withTagValue(CoreMatchers.`is`(getStr(R.string.tagButtonAddBook)))).perform(ViewActions.click())
        onView(withTagValue(CoreMatchers.`is`(getStr(R.string.tagButtonAddDeviceBook)))).perform(ViewActions.click())

        goToTestFiles()
        clickButtonUiAutomator(getStr(R.string.cbzTestFolder))
        clickButtonUiAutomator(getStr(R.string.cbzTestFile))

        onView(withId(R.id.coverImageView)).check(matches(withId(R.id.coverImageView)))
    }
}