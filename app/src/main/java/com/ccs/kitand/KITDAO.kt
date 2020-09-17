package com.ccs.kitand

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

// class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, "customer.db", null, 1)
class KITDAO(context: Context?) : SQLiteOpenHelper(context, "kdb.sqlite", null, 1) {
    lateinit var db: SQLiteDatabase

    // On first launch the data tables do not exist, so create all the tables and the initial Bible record
    override fun onCreate(db: SQLiteDatabase) {
        // Keep a reference to kdb.sqlite
        this.db = db
        // Create the Bibles table
        val sqlBibT = "CREATE TABLE " + TAB_Bibles + "(" +
                COL_BibleID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BibleName + " TEXT, " +
                COL_BookRecsCr + " BOOL, " +
                COL_CurrentBook + " INT)"
        db.execSQL(sqlBibT)
        // Create the Books table
        val sqlBookT = "CREATE TABLE " + TAB_Books + "(" +
                COL_BookID + " INT, " +
                COLF_BibID + " INT, " +
                COL_BookCode + " TEXT, " +
                COL_BookName + " TEXT, " +
                COL_ChapRecsCr + " BOOL, " +
                COL_NumChaps + " INT, " +
                COL_CurrentChap + " INT)"
        db.execSQL(sqlBookT)
        // Create the Chapters table
        val sqlChapT = "CREATE TABLE " + TAB_Chapters + "(" +
                COL_ChapterID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLF_ChBibID + " INT, " +
                COLF_BookID + " INT, " +
                COL_ChapNum + " INT, " +
                COL_ItemRecsCr + " BOOL, " +
                COL_NumVerses + " INT, " +
                COL_NumItems + " INT, " +
                COL_CurrItem + " INT, " +
                COL_USFMText + " TEXT)"
        db.execSQL(sqlChapT)
        // Create the VerseItems table
        val sqlVerseT = "CREATE TABLE " + TAB_VerseItems + "(" +
                COL_ItemID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLF_ChapID + " INT, " +
                COL_VerseNum + " INT, " +
                COL_ItemType + " TEXT, " +
                COL_ItemOrder + " INT, " +
                COL_ItemText + " TEXT, " +
                COL_IntSeq + " INT, " +
                COL_IsBridge + " BOOL, " +
                COL_LastVsBridge + " INT)"
        db.execSQL(sqlVerseT)
        // Create the BridgeItems table
        val sqlBridgT = "CREATE TABLE " + TAB_BridgeItems + "(" +
                COL_BridgeID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLF_ItemID + " INT, " +
                COL_TextCurrBridge + " TEXT, " +
                COL_TextExtraVerse + " TEXT)"
        db.execSQL(sqlBridgT)
        // Create the single Bibles record
        val cv = ContentValues()
        cv.put(COL_BibleID, 1)
        cv.put(COL_BibleName, "Bible")
        cv.put(COL_BookRecsCr, false)
        cv.put(COL_CurrentBook, 0)
        val insert = db.insert(TAB_Bibles, null, cv)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun bibNameKey(): String {
        return COL_BibleName
    }
    fun whoAmI() {
        println("Database access via KITDAO")
    }

//--------------------------------------------------------------------------------------------
//	Bibles data table

    // The single record in the Bibles table needs to be read when the app launches to find out
    //	* whether the Books records need to be created (on first launch) or
    //	* what is the current Book (on subsequent launches)

    fun bibleGetRec(): ContentValues {
        this.db = this.getReadableDatabase()
        val sql = "SELECT * FROM " + TAB_Bibles + " WHERE " + COL_BibleID + " = 1"
        val cursor = db.rawQuery(sql, null)
        val cv = ContentValues()
        if (cursor.moveToFirst()) {
            cv.put(COL_BibleID, cursor.getInt(0))
            cv.put(COL_BibleName, cursor.getString(1))
            cv.put(COL_BookRecsCr, if (cursor.getInt(2) == 1) true else false)
            cv.put(COL_CurrentBook, cursor.getInt(3))
        } else {
            // If the read from database has failed, return bibleID = 0. The normal, one and only record
            // in the Bibles table will have bibleID = 1.
            cv.put(COL_BibleID, 0)
        }
        cursor.close()
        return cv
    }
 /*
	func bibleGetRec () -> (bibID:Int, bibName:String, bkRCr:Bool, currBk:Int) {
		var sqlite3_stmt:OpaquePointer?=nil
		let sql:String = "SELECT bibleID, name, bookRecsCreated, currBook FROM Bibles;"
		let nByte:Int32 = Int32(sql.utf8.count)

		sqlite3_prepare_v2(db, sql, nByte, &sqlite3_stmt, nil)
		sqlite3_step(sqlite3_stmt)
		let bID = Int(sqlite3_column_int(sqlite3_stmt, 0))
		let bNamep: UnsafePointer<UInt8>? = sqlite3_column_text(sqlite3_stmt, 1)
		let bNamen = Int(sqlite3_column_bytes(sqlite3_stmt,1))
		let data = Data(bytes: bNamep!, count: Int(bNamen))
		let str = String(data: data, encoding: String.Encoding.utf8)
		let bkC = Int(sqlite3_column_int(sqlite3_stmt, 2))
		let cBk = Int(sqlite3_column_int(sqlite3_stmt, 3))
		return (bID, str!, (bkC > 0 ? true : false), cBk)
	}
*/

    companion object {
        const val TAB_Bibles = "Bibles"
        const val COL_BibleID = "bibleID"
        const val COL_BibleName = "name"
        const val COL_BookRecsCr = "bookRecsCreated"
        const val COL_CurrentBook = "currBook"

        const val TAB_Books = "Books"
        const val COL_BookID = "bookID"
        const val COLF_BibID = "bibleID"
        const val COL_BookCode = "bookCode"
        const val COL_BookName = "bookName"
        const val COL_ChapRecsCr = "chapRecsCreated"
        const val COL_NumChaps = "numChaps"
        const val COL_CurrentChap = "currChapter"

        const val TAB_Chapters = "Chapters"
        const val COL_ChapterID = "chapterID"
        const val COLF_ChBibID = "bibleID"
        const val COLF_BookID = "bookID"
        const val COL_ChapNum = "chapterNumber"
        const val COL_ItemRecsCr = "itemRecsCreated"
        const val COL_NumVerses = "numVerses"
        const val COL_NumItems = "numItems"
        const val COL_CurrItem = "currItem"
        const val COL_USFMText = "USFMText"

        const val TAB_VerseItems = "VerseItems"
        const val COL_ItemID = "itemID"
        const val COLF_ChapID = "chapterID"
        const val COL_VerseNum = "verseNumber"
        const val COL_ItemType = "itemType"
        const val COL_ItemOrder = "itemOrder"
        const val COL_ItemText = "itemText"
        const val COL_IntSeq = "intSeq"
        const val COL_IsBridge = "isBridge"
        const val COL_LastVsBridge = "lastVsBridge"

        const val TAB_BridgeItems = "BridgeItems"
        const val COL_BridgeID = "bridgeID"
        const val COLF_ItemID = "itemID"
        const val COL_TextCurrBridge = "textCurrBridge"
        const val COL_TextExtraVerse = "textExtraVerse"
        
    }
}