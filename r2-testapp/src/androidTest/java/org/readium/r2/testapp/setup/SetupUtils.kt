/**
 * Author: Didier HEMERY
 * Trainee @EDRLab
 * File: SetupUtils.kt
 */

package org.readium.r2.testapp.setup

import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.jetbrains.anko.db.*
import org.readium.r2.testapp.db.BOOKSTable
import org.readium.r2.testapp.db.BooksDatabase
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation


/**
 * Initializes the testing environment by destroying the Books table and creating it again.
 * Would be preferable to have a function to create it in the BooksDatabase.kt file.
 * It also allows permission to access the sdcard.
 * This function allows the tests to run without having to manually configure the app.
 */
fun initTestEnv() {
    val db = BooksDatabase(getInstrumentation().targetContext)
    db.books.dropTable()
    db.shared.use {
        createTable(BOOKSTable.NAME, true,
                BOOKSTable.ID to INTEGER + PRIMARY_KEY  + AUTOINCREMENT,
                BOOKSTable.FILENAME to TEXT,
                BOOKSTable.TITLE to TEXT,
                BOOKSTable.AUTHOR to TEXT,
                BOOKSTable.FILEURL to TEXT,
                BOOKSTable.IDENTIFIER to TEXT,
                BOOKSTable.COVER to BLOB,
                BOOKSTable.COVERURL to TEXT,
                BOOKSTable.EXTENSION to TEXT,
                BOOKSTable.CREATION to INTEGER)
    }
    val perm = UiDevice.getInstance(getInstrumentation()).findObject(UiSelector().text("Allow"))
    if (perm.exists())
        perm.click()
}

/**
 * The function is a shortcut for calling UiAutomator. It performs a click on a view that holds the text contained in 'button'
 *
 * button: String - The view to click on. It is targeted through the text shown on screen.
 */
fun clickButtonUiAutomator(button: String) {
    UiDevice.getInstance(getInstrumentation()).findObject(UiSelector().text(button)).click()
}

/**
 * Creates a separate thread that will wait for 'time'. Then synchronizes.
 *
 * time: Long - Time to wait in milliseconds
 */
fun waitFor(time: Long) {
    val t = Thread(Runnable {Thread.sleep(time)})
    t.start()
    t.join()
}

/**
 * Gets resource string for the given ID.
 *
 * strID: Int - The resource string ID.
 */
fun getStr(strID: Int) : String {
    return getInstrumentation().targetContext.getString(strID)
}