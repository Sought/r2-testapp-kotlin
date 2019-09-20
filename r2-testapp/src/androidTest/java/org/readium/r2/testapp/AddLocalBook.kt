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
import org.readium.r2.testapp.setup.*


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
        remPubsFromDeviceInternalMemory()
    }

    /**
     * Once the device's file explorer is open, navigate into the test files' folder.
     */
    private fun selectFileInExplorer(pub: String) {
        //TODO: add a variant for each test device used, as the UI changes on different versions.
        //    It might need another switch rather than the build sdk version.

        when (Build.VERSION.SDK_INT){
            23 -> {
                //ANDROID 6 TABLET
                clickButtonUiAutomator(getStr(R.string.InternalStorage_SDK23))
                scrollUntilFoundTextAndClickUiAutomator(getStr(R.string.Folder1))
                clickButtonUiAutomator(getStr(R.string.Folder2))
                scrollUntilFoundTextAndClickUiAutomator(getStr(R.string.Folder3))
                clickButtonUiAutomator(getStr(R.string.Folder4))
                clickButtonUiAutomator(pub)
            }
            else -> throw Exception(getStr(R.string.UnsupportedVersionTest))
        }

    }

    /**
     * Tests that importing a publication works. Resets the imported database, imports the
     * publication then checks that a coverImageView exists. It currently does not check if the
     * cover is the right one. It might confuse the test if multiple imageViews are present (and
     * make it fail even if the file was added).
     */
    fun importTestPublicationWorks(pub: String) {
        copyPubFromAPKToDeviceInternalMemory(pub)
        onView(withTagValue(CoreMatchers.`is`(getStr(R.string.tagButtonAddBook)))).perform(ViewActions.click())
        onView(withTagValue(CoreMatchers.`is`(getStr(R.string.tagButtonAddDeviceBook)))).perform(ViewActions.click())
        selectFileInExplorer(pub)
        waitFor(5000)
        onView(withId(R.id.coverImageView)).check(matches(withId(R.id.coverImageView)))
    }

    /**
     * Tests if a Local CBZ that is valid can be added.
     */
    @Test
    fun importLocalCBZPWorks() {
        importTestPublicationWorks(getStr(R.string.cbzTestFile))
    }

    /**
     * Tests if a Local EPub that is valid can be added.
     */
    @Test
    fun importLocalEPubWorks() {
        importTestPublicationWorks(getStr(R.string.epubTestFile))
    }

    /**
     * Tests if a Local Audiobook that is valid can be added.
     */
    @Test
    fun importLocalAudioBookWorks() {
        importTestPublicationWorks(getStr(R.string.audiobookTestFile))
    }

    /**
     * Tests if a Local DiViNa webtoon with manifest that is valid can be added.
     */
    @Test
    fun importLocalDivinaManifestWebtoonWorks() {
        importTestPublicationWorks(getStr(R.string.webtoonManifestDiViNaTestFile))
    }

    /**
     * Tests if a Local DiViNa webtoon with publication that is valid can be added.
     */
    @Test
    fun importLocalDivinaPublicationWebtoonWorks() {
        importTestPublicationWorks(getStr(R.string.webtoonPublicationDiViNaTestFile))
    }

    /**
     * Tests if a Local DiViNa turbomedia with manifest that is valid can be added.
     */
    @Test
    fun importLocalDivinaManifestTurbomediaWorks() {
        importTestPublicationWorks(getStr(R.string.turbomediaManifestDiViNaTestFile))
    }

    /**
     * Tests if a Local DiViNa turbomedia with publication that is valid can be added.
     */
    @Test
    fun importLocalDivinaPublicationTurbomediaWorks() {
        importTestPublicationWorks(getStr(R.string.turbomediaPublicationDiViNaTestFile))
    }
}