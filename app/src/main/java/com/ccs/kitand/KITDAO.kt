package com.ccs.kitand

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import java.io.File

//  Created by Graeme Costin on 12/8/2020.
// The author disclaims copyright to this source code.  In place of
// a legal notice, here is a blessing:
//
//    May you do good and not evil.
//    May you find forgiveness for yourself and forgive others.
//    May you share freely, never taking more than you give.
//
//	All interaction between the running app and the SQLite database is handled by this class.
//	The rest of the app can treat the SQLite database as a software object with interaction
//	directed through the member functions of the KITDAO class which is named from the phrase
//	KIT Data Access Object.
//
//	Parameters passed to KITDAO's functions are in the natural types for the programming
//	language of the rest of the app; any conversion to or from data types that SQLite requires
//	is handled within this class.
//
//	This class is instantiated at the launching of the app and it opens a connection to the
//	database, keeps that connection in the instance property db and retains it until the app
//	terminates. Only one instance of the class is used.
//
//	TODO: Check whether interruption of the app (such as by a phone call coming to the
//	smartphone) needs the database connection to be closed and then reopened when the app
//	returns to the foreground.

class KITDAO {
    val dbName = "kdb.sqlite"
    val dbPath = "/data/user/0/com.ccs.kitand/databases/"
    var db: SQLiteDatabase? = null

    fun whoAmI () {
        println ("Database name = $dbName")
    }

    public fun start() {
        val dbPathname = dbPath + dbName
        db = SQLiteDatabase.openOrCreateDatabase(dbPathname, null)
    }
    // if table Bibles does not exist in db then create all the tables and the initial Bible record
    //SELECT name FROM sqlite_master WHERE type='table' AND name='yourTableName';
//    val tables = SQLiteDatabase.findEditTable("Bibles")
//    if (tables.isEmpty()) {
//        // Create all tables and the default Bible record
//    } else {
//        // init complete, db refers to open database
//    }

}