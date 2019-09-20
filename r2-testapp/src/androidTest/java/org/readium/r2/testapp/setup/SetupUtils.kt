/**
 * Author: Didier HEMERY
 * Trainee @EDRLab
 * File: SetupUtils.kt
 */

package org.readium.r2.testapp.setup

import android.util.Log
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.jetbrains.anko.db.*
import org.readium.r2.testapp.db.BOOKSTable
import org.readium.r2.testapp.db.BooksDatabase
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiScrollable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


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
    //TODO: Invalidate view (postInvalidate?) to redraw everything and not have some tests fail.
}

/**
 * Writes the file in the androidTest app assets folder to the device's internal storage.
 * @ /Android/data/org.readium.r2reader.test
 * Logs error if it happens.
 *
 * pub: String - The name of the publication to add to internal memory.
 */
fun copyPubFromAPKToDeviceInternalMemory(pub: String) {
    try {
        Log.w("PATH", getInstrumentation().context.getExternalFilesDir(null)!!.absolutePath)
        val file = File(getInstrumentation().context.getExternalFilesDir(null), pub)
        val ins = getInstrumentation().context.assets.open(pub)
        val outs = FileOutputStream(file)
        var data = ByteArray(ins.available())
        ins.read(data)
        outs.write(data)
        ins.close()
        outs.close()
    } catch (e: IOException) {
        Log.e("IO ERROR", e.stackTrace.toString())
    }
}

/**
 * Removes every single file in the device's internal storage
 * @ /Android/data/org.readium.r2reader.test
 */
fun remPubsFromDeviceInternalMemory() {
    try {
        getInstrumentation().context.getExternalFilesDir(null)!!.walk().forEach {
            it.delete()
        }
    } catch (e: IOException) {
        Log.e("IO ERROR", e.stackTrace.toString())
    }
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
 * Uses UiAutomator to scroll a scrollable view until the text searched appears.
 * clickButtonUiAutomator is then called with the text to click on.
 *
 * test:String - The text the function should scroll to and click.
 */
fun scrollUntilFoundTextAndClickUiAutomator(text: String) {
    UiScrollable(UiSelector().scrollable(true)).scrollIntoView(UiSelector().text(text))
    clickButtonUiAutomator(text)
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