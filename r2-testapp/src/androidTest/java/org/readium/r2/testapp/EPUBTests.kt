package org.readium.r2.testapp

import android.app.Activity
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import android.webkit.WebView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.readium.r2.testapp.library.LibraryActivity
import org.readium.r2.testapp.setup.addPubToDatabase
import org.readium.r2.testapp.setup.copyPubFromAPKToDeviceInternalMemory
import org.readium.r2.testapp.setup.getStr
import org.readium.r2.testapp.setup.initTestEnv
import org.readium.r2.testapp.setup.remPubsFromDeviceInternalMemory
import org.readium.r2.testapp.setup.scrollUntilFoundTextAndClickUiAutomator
import org.readium.r2.testapp.setup.waitFor

@RunWith(AndroidJUnit4::class)
@LargeTest
class EPUBTests {
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
     * Get the current running LibraryActivity.
     */
    private fun getActivity(): Activity? {
        var activity: LibraryActivity? = null
        activityScenarioRule.scenario.onActivity {
            activity = it
        }
        return activity
    }

    /**
     * Copy file from internal app storage to external storage.
     * Add the selected epub to the database.
     * Click on the novel's button.
     *
     * @param pub: String - The name of the file in internal memory.
     */
    private fun addTestEPUB(pub: String) {
        copyPubFromAPKToDeviceInternalMemory(pub)
        addPubToDatabase(pub, getActivity() as LibraryActivity)
        waitFor(1000)

        onView(withText(getStr(R.string.epubTestName))).perform(ViewActions.click())
    }

    /**
     * Returns the whole text (without html) contained inside a webview.
     */
    private fun getWebViewStr(): String {
        val mDevice : UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val webView = mDevice.findObject(By.clazz(WebView::class.java))
        val str = listAllChildren(webView)
        Log.e("WebView text", str)
        return str
    }

    /**
     * Appends all the parts of the text together.
     *
     * @param obj: UiObject2 - The ui element containing the text we want.
     */
    private fun listAllChildren(obj: UiObject2): String {
        var str = obj.text

        for (a in obj.children)
            str += "\n" + listAllChildren(a)

        return str
    }

    /**
     * Perform a swipe action right to left on given view ID.
     *
     * @param id: Int - The id of the object to swipe on.
     */
    private fun swipeRTL(id: Int) {
        onView(withId(id)).perform(GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT, Press.FINGER))
    }

    /**
     * Perform a swipe action left to right on the view with the given ID.
     *
     * @param id: Int - The id of the object to swipe on.
     */
    private fun swipeLTR(id: Int) {
        onView(withId(id)).perform(GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT, Press.FINGER))
    }

    /**
     * Perform a single click action on the view with the given ID.
     *
     * @param id: Int - The id of the object to swipe on.
     */
    private fun clickCenter(id: Int) {
        onView(withId(id)).perform(GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER, Press.FINGER,
            InputDevice.SOURCE_ANY, MotionEvent.BUTTON_PRIMARY))
    }

    /**
     * Perform all the UI actions to access TTS interface.
     */
    private fun goToTTS() {
        waitFor(1000)
        clickCenter(R.id.resourcePager)
        onView(withId(R.id.screen_reader)).perform(ViewActions.click())
    }

    /**
     * Perform all the UI actions to access the TOC interface.
     */
    private fun goToTOC() {
        waitFor(1000)
        clickCenter(R.id.resourcePager)
        onView(withId(R.id.toc)).perform(ViewActions.click())
    }

    private fun goToSettings() {
        waitFor(1000)
        clickCenter(R.id.resourcePager)
        onView(withId(R.id.settings)).perform(ViewActions.click())
    }

    /**
     * Perform all the UI actions to add a book mark and go to the bookmark interface.
     */
    private fun addBookmarkAndGoToBookmarks() {
        waitFor(1000)
        clickCenter(R.id.resourcePager)
        onView(withId(R.id.bookmark)).perform(ViewActions.click())
        onView(withId(R.id.toc)).perform(ViewActions.click())
        //onView(withId(R.id.bookmarks_tab)).perform(ViewActions.click())
        onView(withText("Bookmarks")).perform(ViewActions.click())
    }

    /**
     * Perform all the UI actions to access the Search interface.
     */
    private fun goToSearch() {
        waitFor(1000)
        clickCenter(R.id.resourcePager)
        onView(withId(R.id.search)).perform(ViewActions.click())
    }

    /**
     * Test that swiping right to left page 1 behaves correctly.
     */
    @Test
    fun swipeRTLBeginning() {
        addTestEPUB(getStr(R.string.epubTestFile))
        swipeRTL(R.id.resourcePager)
        assertTrue(getWebViewStr().contains("Jeanne Loiseau"))
    }

    /**
     * Test that swiping left to right page 1 behaves correctly.
     */
    @Test
    fun swipeLTRBeginning() {
        addTestEPUB(getStr(R.string.epubTestFile))
        swipeLTR(R.id.resourcePager)
        assertTrue(getWebViewStr().contains("Couverture"))
    }

    /**
     * Test launching TTS.
     */
    @Test
    fun ttsLaunch() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTTS()
        onView(withId(R.id.play_pause)).check(matches(isDisplayed()))
    }

    /**
     * Test that many swipes in succession will not kill the app
     */
    @Test
    fun infiniteSwipe() {
        var run = true

        addTestEPUB(getStr(R.string.epubTestFile))
        waitFor(1000)

        val t = Thread(Runnable {
            Thread.sleep(10000)
            run = false
        })

        t.start()
        var cycle = false
        while (run) {
            if (cycle) {
                cycle = false
                swipeLTR(R.id.resourcePager)
            } else {
                cycle = true
                swipeRTL(R.id.resourcePager)
            }
        }
        t.join()
    }

    /**
     * Test launching TTS and quitting it.
     */
    @Test
    fun ttsLaunchAndQuit() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTTS()
        onView(withId(R.id.screen_reader)).perform(ViewActions.click())
        onView(withId(R.id.play_pause)).check(matches(not(isDisplayed())))
    }
/*
    /**
     *
     */
    @Test
    fun ttsTextVisible() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTTS()

        onView(withId(R.id.TextAlignment)).perform(ViewActions.click())
    }

    /**
     *
     */
    @Test
    fun ttsPressPlayPause() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTTS()


    }

    /**
     *
     */
    @Test
    fun ttwSwipe() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTTS()

    }

    /**
     *
     */
    @Test
    fun ttsSwipeAndPressPlayPause() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTTS()

    }



    /**
     *
     */
    @Test
    fun ttsSwipeAndPressPlayPauseMultiple() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTTS()

    }
    */

    /**
     * Test that going to the first chapter through the TOC works.
     */
    @Test
    fun contentGoToFirstChapter() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTOC()
        scrollUntilFoundTextAndClickUiAutomator("Chapitre I")
        assertTrue(getWebViewStr().contains("Chapitre I"))
    }

    /**
     * Test that going to the last chapter through the TOC works.
     */
    @Test
    fun contentGoToEndChapter() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTOC()
        scrollUntilFoundTextAndClickUiAutomator("À propos de cette édition électronique")
        assertTrue(getWebViewStr().contains("À propos de cette édition électronique"))
    }

    /**
     * Test that going to the middle chapter through the TOC works.
     */
    @Test
    fun contentGoToMiddleChapter() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToTOC()
        scrollUntilFoundTextAndClickUiAutomator("Chapitre IX")
        assertTrue(getWebViewStr().contains("Chapitre IX"))
    }

    /**
     * Test bookmarking a page and jumping to it.
     */
    @Test
    fun bookmarkSimple() {
        addTestEPUB(getStr(R.string.epubTestFile))
        addBookmarkAndGoToBookmarks()
        onView(withText("ch1")).perform(ViewActions.click())
        assertTrue(getWebViewStr().contains("Couverture"))
    }

    /**
     * Test bookmarking a page and then deleting it.
     */
    @Test
    fun bookmarkAndDelete() {
        addTestEPUB(getStr(R.string.epubTestFile))
        addBookmarkAndGoToBookmarks()
        onView(withId(R.id.overflow)).perform(ViewActions.longClick())
        onView(withText("DELETE")).perform(ViewActions.click())
        onView(withId(R.id.overflow)).check(doesNotExist())
    }

    /**
     * Test that bookmarking twice at the same page does not generate two bookmarks.
     */
    @Test
    fun bookmarkSamePageTwice() {
        addTestEPUB(getStr(R.string.epubTestFile))
        waitFor(1000)
        clickCenter(R.id.resourcePager)
        onView(withId(R.id.bookmark)).perform(ViewActions.click())
        clickCenter(R.id.resourcePager)
        addBookmarkAndGoToBookmarks()
        onView(withId(R.id.overflow)).check(matches(isDisplayed())) //fails if present 2 times or more
    }

    /**
     * Test that the font can be changed.
     */
    @Test
    fun settingsChangeFont() {
        addTestEPUB(getStr(R.string.epubTestFile))

        swipeRTL(R.id.resourcePager)
        waitFor(500)
        swipeRTL(R.id.resourcePager)
        waitFor(500)

        clickCenter(R.id.resourcePager)
        onView(withId(R.id.settings)).perform(ViewActions.click())
        onView(withId(R.id.spinner_action_settings_intervall_values)).perform(ViewActions.click())
        onView(withText("Roboto")).perform(ViewActions.click())

        pressBack()
        waitFor(500)
        clickCenter(R.id.resourcePager)

        assertTrue(getWebViewStr().contains("Les regards se parlaient, à défaut des bouches."))
    }

    /**
     * Test that the background selection can be used without crashing the app. The test does not check the result.
     */
    @Test
    fun settingsChangeBackground() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()
        onView(withId(R.id.appearance_sepia)).perform(ViewActions.click())
        pressBack()
        clickCenter(R.id.resourcePager)
    }

    /**
     * Test that enabling scrolling and scrolling through a chapter works.
     */
    @Test
    fun settingsEnableScrollModeAndScroll() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()
        onView(withId(R.id.scroll_mode)).perform(ViewActions.click())
        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
        UiScrollable(UiSelector().scrollable(true)).scrollIntoView(UiSelector().textContains("Très bien. Nous verrons, dit Lucien."))
        assertTrue(getWebViewStr().contains("Très bien. Nous verrons, dit Lucien."))
    }

    /**
     *
     */
    @Test
    fun settingsTextJustified() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()
        onView(withText("Advanced")).perform(ViewActions.click())
        onView(withId(R.id.alignment_justify)).perform(ViewActions.click())
        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)

    }

    @Test
    fun settingsTextLeft() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()
        onView(withText("Advanced")).perform(ViewActions.click())
        onView(withId(R.id.alignment_left)).perform(ViewActions.click())
        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsAutoColumns() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())
        onView(withId(R.id.column_auto)).perform(ViewActions.click())
        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsOneColumn() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())
        onView(withId(R.id.column_one)).perform(ViewActions.click())
        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsTwoColumns() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())
        onView(withId(R.id.column_two)).perform(ViewActions.click())
        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsMinPageMargin() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())

        onView(withId(R.id.pm_decrease)).perform(ViewActions.click())
        onView(withId(R.id.pm_decrease)).perform(ViewActions.click())
        onView(withId(R.id.pm_decrease)).perform(ViewActions.click())
        onView(withId(R.id.pm_decrease)).perform(ViewActions.click())
        onView(withId(R.id.pm_decrease)).perform(ViewActions.click())
        onView(withId(R.id.pm_decrease)).perform(ViewActions.click())

        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsMaxPageMargin() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())

        onView(withId(R.id.pm_increase)).perform(ViewActions.click())
        onView(withId(R.id.pm_increase)).perform(ViewActions.click())
        onView(withId(R.id.pm_increase)).perform(ViewActions.click())
        onView(withId(R.id.pm_increase)).perform(ViewActions.click())
        onView(withId(R.id.pm_increase)).perform(ViewActions.click())
        onView(withId(R.id.pm_increase)).perform(ViewActions.click())
        onView(withId(R.id.pm_increase)).perform(ViewActions.click())
        onView(withId(R.id.pm_increase)).perform(ViewActions.click())

        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsMinWordSpacing() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())
        onView(withId(R.id.ws_decrease)).perform(ViewActions.click())
        pressBack()


        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsMaxWordSpacing() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())

        onView(withId(R.id.ws_increase)).perform(ViewActions.click())
        onView(withId(R.id.ws_increase)).perform(ViewActions.click())

        pressBack()


        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsMinLetterSpacing() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())
        onView(withId(R.id.ls_decrease)).perform(ViewActions.click())
        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsMaxLetterSpacing() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())

        onView(withId(R.id.ls_increase)).perform(ViewActions.click())
        onView(withId(R.id.ls_increase)).perform(ViewActions.click())
        onView(withId(R.id.ls_increase)).perform(ViewActions.click())
        onView(withId(R.id.ls_increase)).perform(ViewActions.click())
        onView(withId(R.id.ls_increase)).perform(ViewActions.click())
        onView(withId(R.id.ls_increase)).perform(ViewActions.click())
        onView(withId(R.id.ls_increase)).perform(ViewActions.click())
        onView(withId(R.id.ls_increase)).perform(ViewActions.click())

        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsMinLineHeight() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())
        onView(withId(R.id.lh_decrease)).perform(ViewActions.click())
        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun settingsMaxLineHeight() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSettings()

        onView(withText("Advanced")).perform(ViewActions.click())

        onView(withId(R.id.lh_increase)).perform(ViewActions.click())
        onView(withId(R.id.lh_increase)).perform(ViewActions.click())
        onView(withId(R.id.lh_increase)).perform(ViewActions.click())
        onView(withId(R.id.lh_increase)).perform(ViewActions.click())

        pressBack()

        swipeRTL(R.id.resourcePager)
        swipeRTL(R.id.resourcePager)
    }

    @Test
    fun searchSimpleNoResult() {
        addTestEPUB(getStr(R.string.epubTestFile))
        goToSearch()
        onView(with).perform(ViewActions.typeText("alibaba"))
        onView(withId(R.id.text)).perform(ViewActions.pressImeActionButton())
        waitFor(20000)
        //onView(withTe)
    }

    @Test
    fun searchLongNoResult() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchSimpleResult() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchLongResult() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchBackslash() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchDoubleQuotes() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchDoubleSlash() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchTwoBackSlash() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchCyrillic() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchKanji() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchArab() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }

    @Test
    fun searchEmoji() {
        addTestEPUB(getStr(R.string.epubTestFile))

    }
}