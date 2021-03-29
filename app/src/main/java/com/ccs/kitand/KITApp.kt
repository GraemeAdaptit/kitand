package com.ccs.kitand

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView

//  Created by Graeme Costin on 3JUL20.
// The author disclaims copyright to this source code.  In place of
// a legal notice, here is a blessing:
//
//    May you do good and not evil.
//    May you find forgiveness for yourself and forgive others.
//    May you share freely, never taking more than you give.
//
// KITApp functions in a similar manner to the AppDelegate in kitios because it enables
// some important class instances to be accessed from many parts of the app.

class KITApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        var instance: KITApp? = null
            private set

        lateinit var res: Resources
        lateinit var dao: KITDAO        // For access to kdb.sqlite
        lateinit var bibInst: Bible     // For access to the single instance of Bible
        lateinit var bkInst: Book       // For access to the instance of the currently selected Book
        lateinit var chInst: Chapter    // For access to the instance of the currently selected Chapter
        // TODO: Is vItAda really necessary? Remove it if possible
        lateinit var vItAda: VerseItemAdapter   // For access to the instance of the VerseItemAdapter
        // TODO: Is recycV really necessary? Remove it if possible
        lateinit var recycV: RecyclerView       // For access to the RecyclerView of EditChapterActivity
    }
}
