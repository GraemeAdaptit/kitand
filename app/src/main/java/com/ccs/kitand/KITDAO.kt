package com.ccs.kitand

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//  Created by Graeme Costin on 12AUG20.
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
    private lateinit var db: SQLiteDatabase

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

//--------------------------------------------------------------------------------------------
//	Bibles data table

    // The single record in the Bibles table needs to be read when the app launches to find out
    //	* whether the Books records need to be created (on first launch) or
    //	* what is the current Book (on subsequent launches)
    //
    //  Return values
    // cv("1") = COL_BibleID
    // cv("2") = COL_BibleName
    // cv("3") = COL_BookRecsCr
    // cv("4") = COL_CurrentBook

    fun bibleGetRec(): ContentValues {
        this.db = this.getReadableDatabase()
        val sql = "SELECT * FROM " + TAB_Bibles + " WHERE " + COL_BibleID + " = 1"
        val cursor = db.rawQuery(sql, null)
        val cv = ContentValues()
        if (cursor.moveToFirst()) {
            cv.put("1", cursor.getInt(0))
            cv.put("2", cursor.getString(1))
            cv.put("3", if (cursor.getInt(2) == 1) true else false)
            cv.put("4", cursor.getInt(3))
        } else {
            // If the read from database has failed, return bibleID = 0. The normal, one and only record
            // in the Bibles table will have bibleID = 1.
            cv.put(COL_BibleID, 0)
        }
        cursor.close()
        return cv
    }
	// The single Bible record needs to be updated
	//  * to set a new name for the Bible at the user's command
	//	* to set the flag that indicates that the Books records have been created (on first launch)
	//	* to change the current Book whenever the user selects a different Book to work on

	// This function needs a String parameter for the revised Bible name
	fun bibleUpdateName(bibName:String): Boolean {
		this.db = this.getWritableDatabase()
		val cv = ContentValues()
		cv.put(COL_BibleName, bibName)
		val rows = db.update(TAB_Bibles, cv, COL_BibleID + " = 1", null)
        return (rows == 1)
	}

	// The bookRecsCreated flag starts as false and is changed to true during the first launch;
	// it is never changed back to false, and so this function does not need any parameters.
	fun bibleUpdateRecsCreated():Boolean {
    	this.db = this.getWritableDatabase()
		val cv = ContentValues()
		cv.put(COL_BookRecsCr, true)
		val rows = db.update(TAB_Bibles, cv, COL_BibleID + " = 1", null)
        return rows == 1
	}


	// This function needs an Integer parameter for the current Book
	fun bibleUpdateCurrBook (currBk:Int): Boolean {
        this.db = this.getWritableDatabase()
        val cv = ContentValues()
        cv.put(COL_CurrentBook, currBk)
        val rows = db.update(TAB_Bibles, cv, COL_BibleID + " = 1", null)
        return (rows == 1)
	}

    //--------------------------------------------------------------------------------------------
    //	Books data table

    // The 66 records for the Books table need to be created and populated on the initial launch of the app
    // This function will be called 66 times by the KIT software

    fun booksInsertRec (bkID:Int, bibID:Int, bkCode:String, bkName:String, chRCr:Boolean, numCh:Int, currCh:Int): Boolean {
        this.db = this.getWritableDatabase()
        val cv = ContentValues()
        cv.put(COL_BookID, bkID)
        cv.put(COLF_BibID, bibID)
        cv.put(COL_BookCode, bkCode)
        cv.put(COL_BookName, bkName)
        cv.put(COL_ChapRecsCr, chRCr)
        cv.put(COL_NumChaps, numCh)
        cv.put(COL_CurrentChap, currCh)
        val insert = db.insert(TAB_Books, null, cv)
        return (insert > 0L)
    }

    // The Books records need to be read to populate the array of books for the Bible
    // that the user can choose from. They need to be sorted in ascending order of the
    // UBS assigned bookID.
    //
    //  Returns column values via the call back function appendBibBookToArray()

    fun readBooksRecs(bibInst: Bible) {
        this.db = this.getReadableDatabase()
        val sql = "SELECT * FROM " + TAB_Books + " WHERE " + COLF_BibID + " = 1"
        val cursor = db.rawQuery(sql, null)
        cursor.moveToFirst()
        do {
            val bkID = cursor.getInt(0)
            val bibID = cursor.getInt(1)
            val bkCode = cursor.getString(2)
            val bkName = cursor.getString(3)
            val chRCr = if (cursor.getInt(4) == 1) true else false
            val numCh = cursor.getInt(5)
            val curCh = cursor.getInt(6)
            bibInst.appendBibBookToArray(bkID, bibID, bkCode, bkName, chRCr, numCh, curCh)
        } while (cursor.moveToNext())
        cursor.close()
    }

	// The Books record for the current Book needs to be updated
	//	* to set the flag that indicates that the Chapter records have been created (on first edit of that Book)
	//	* to set the number of Chapters in the Book (on first edit of that Book)
	//	* to change the current Chapter when the user selects a different Chapter to work on

	fun booksUpdateRec (bibID:Int, bkID:Int, chRCr:Boolean, numCh:Int, currCh:Int): Boolean {
		this.db = this.getWritableDatabase()
		val cv = ContentValues()
		cv.put(COL_ChapRecsCr, chRCr)
		cv.put(COL_NumChaps, numCh)
		cv.put(COL_CurrentChap, currCh)
        val whArray = arrayOf<String>(bkID.toString())
		val rows = db.update(TAB_Books, cv, COL_BibleID + " = 1 AND " + COL_BookID + " = ?", whArray)
        return (rows == 1)
	}


	//--------------------------------------------------------------------------------------------
	//	Chapters data table

	// The Chapters records for the current Book need to be created when the user first selects that
    // Book to edit.
	// This function will be called once by the KIT software for every Chapter in the current Book;
    // it will be called before any VerseItem Records have been created for the Chapter.
    // Each Chapters record has an INTEGER PRIMARY KEY, chapterID, that is assigned automatically
    // by SQLite; this is not included in the insert record SQL.
    // The field for USFM is left empty until the user taps the "Export" button after
    // keyboarding enough to export.

	fun chaptersInsertRec (bibID:Int, bkID:Int, chNum:Int, itRCr:Boolean, numVs:Int, numIt:Int, currIt:Int): Boolean {
        this.db = this.getWritableDatabase()
        val cv = ContentValues()
        cv.put(COLF_ChBibID, bibID)
        cv.put(COLF_BookID, bkID)
        cv.put(COL_ChapNum, chNum)
        cv.put(COL_ItemRecsCr, itRCr)
        cv.put(COL_NumVerses, numVs)
        cv.put(COL_NumItems, numIt)
        cv.put(COL_CurrItem, currIt)
        val insert = db.insert(TAB_Chapters, null, cv)
        return (insert > 0L)
	}

	// The Chapters records for the currently selected Book need to be read to populate the array
	// of Chapters for the Book bkInst that the user can choose from. The records need to be sorted
	// in ascending order of chapterNumber

	fun readChaptersRecs (bibID:Int, bkInst:Book) {
        this.db = this.getReadableDatabase()
        val sql1 = "SELECT chapterID, bibleID, bookID, chapterNumber, itemRecsCreated, numVerses, numItems, currItem FROM " + TAB_Chapters
        val sql2 =  " WHERE " + COLF_ChBibID + " = ? AND " + COLF_BookID + " = ? ORDER BY " + COL_ChapNum
        val sql = sql1 + sql2
        val whArray = arrayOf<String>(bibID.toString(), bkInst.bkID.toString())
        val cursor = db.rawQuery(sql, whArray)
        cursor.moveToFirst()
        do {
            val chapID = cursor.getInt(0)
            val biblID = cursor.getInt(1)
            val bookID = cursor.getInt(2)
            val chNum = cursor.getInt(3)
            val itRCr = if (cursor.getInt(4) == 1) true else false
            val numVs = cursor.getInt(5)
            val numIt = cursor.getInt(6)
            val curIt = cursor.getInt(7)
            bkInst.appendChapterToArray(chapID, biblID, bookID, chNum, itRCr, numVs, numIt, curIt)
        } while (cursor.moveToNext())
        cursor.close()
	}


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