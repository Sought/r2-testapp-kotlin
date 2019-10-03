/**
 * Author: Didier HEMERY
 * Trainee @EDRLab
 * File: AddLocalBook.kt
 */

package org.readium.r2.testapp

import android.os.Build
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.readium.r2.testapp.setup.clickButtonUiAutomator
import org.readium.r2.testapp.setup.copyPubFromAPKToDeviceInternalMemory
import org.readium.r2.testapp.setup.getStr
import org.readium.r2.testapp.setup.initTestEnv
import org.readium.r2.testapp.setup.remPubsFromDeviceInternalMemory
import org.readium.r2.testapp.setup.scrollUntilFoundTextAndClickUiAutomator
import org.readium.r2.testapp.setup.waitFor


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
     * Reset the imported database, imports the publication then waits some time.
     */
    fun importTestPublication(pub: String) {
        copyPubFromAPKToDeviceInternalMemory(pub)
        onView(withTagValue(CoreMatchers.`is`(getStr(R.string.tagButtonAddBook)))).perform(ViewActions.click())
        onView(withTagValue(CoreMatchers.`is`(getStr(R.string.tagButtonAddDeviceBook)))).perform(ViewActions.click())
        selectFileInExplorer(pub)
        waitFor(1000)
    }

    /**
     * Test that a valid publication could be imported by checking that there is a cover image view.
     */
    fun importTestPublicationWorks(pub: String) {
        importTestPublication(pub)
        onView(withId(R.id.coverImageView)).check(matches(withId(R.id.coverImageView)))
    }

    /**
     * Test that an invalid publication could not be imported by checking that.
     */
    fun importTestPublicationFail(pub: String) {
        importTestPublication(pub)
        onView(withId(R.id.coverImageView)).check(doesNotExist())
    }

    /**
     * Test if a Local CBZ that is valid can be added.
     */
    @Test
    fun importLocalCBZPWorks() {
        importTestPublicationWorks(getStr(R.string.cbzTestFile))
    }

    /**
     * Test if a Local EPub that is valid can be added.
     */
    @Test
    fun importLocalEPubWorks() {
        importTestPublicationWorks(getStr(R.string.epubTestFile))
    }

    /**
     * Test if a Local Audiobook that is valid can be added.
     */
    @Test
    fun importLocalAudioBookWorks() {
        importTestPublicationWorks(getStr(R.string.audiobookTestFile))
    }

    /**
     * Test if a Local DiViNa webtoon with manifest that is valid can be added.
     */
    @Test
    fun importLocalDivinaManifestWebtoonWorks() {
        importTestPublicationWorks(getStr(R.string.webtoonManifestDiViNaTestFile))
    }

    /**
     * Test if a Local DiViNa webtoon with publication that is valid can be added.
     */
    @Test
    fun importLocalDivinaPublicationWebtoonWorks() {
        importTestPublicationWorks(getStr(R.string.webtoonPublicationDiViNaTestFile))
    }

    /**
     * Test if a Local DiViNa turbomedia with manifest that is valid can be added.
     */
    @Test
    fun importLocalDivinaManifestTurbomediaWorks() {
        importTestPublicationWorks(getStr(R.string.turbomediaManifestDiViNaTestFile))
    }

    /**
     * Test if a Local DiViNa turbomedia with publication that is valid can be added.
     */
    @Test
    fun importLocalDivinaPublicationTurbomediaWorks() {
        importTestPublicationWorks(getStr(R.string.turbomediaPublicationDiViNaTestFile))
    }

    /**
     * Running these tests will keep the app running. Once the issue that make them keep the app busy
     * is taken care of, they should be un-commented then.
     */
    /*
    /**
     * Test if a local Invalid CBZ fails as it should.
     */
    @Test
    fun importLocalCBZPublicationFail() {
        importTestPublicationFail(getStr(R.string.invalidCBZFile))
    }

    /**
     * Test if a local Invalid Epub (empty zipped) fails as it should.
     */
    @Test
    fun importLocalEpubPublicationFail() {
        importTestPublicationFail(getStr(R.string.invalidEPubFile))
    }

    /**
     * Test if a local Invalid Audiobook (empty zipped) fails as it should.
     */
    @Test
    fun importLocalAudiobookPublicationFail() {
        importTestPublicationFail(getStr(R.string.invalidAudioBookFile))
    }

    /**
     * Test if a local Invalid DiViNa (empty zipped) fails as it should.
     */
    @Test
    fun importLocalDivinaPublicationFail() {
        importTestPublicationFail(getStr(R.string.invalidDiViNaFile))
    }

    /**
     * Test if a local Invalid empty CBZ fails as it should.
     */
    @Test
    fun importLocalEmptyCBZPublicationFail() {
        importTestPublicationFail(getStr(R.string.invalidEmptyCBZFile))
    }

    /**
     * Test if a local Invalid empty Epub fails as it should.
     */
    @Test
    fun importLocalEmptyEpubPublicationFail() {
        importTestPublicationFail(getStr(R.string.invalidEmptyEPubFile))
    }

    /**
     * Test if a local Invalid empty Audiobook fails as it should.
     */
    @Test
    fun importLocalEmptyAudiobookPublicationFail() {
        importTestPublicationFail(getStr(R.string.invalidEmptyAudioBookFile))
    }

    /**
     * Test if a local Invalid empty DiViNa fails as it should.
     */
    @Test
    fun importLocalEmptyDivinaPublicationFail() {
        importTestPublicationFail(getStr(R.string.invalidEmptyDiViNaFile))
    }
    */
}