package com.ccs.kitand

import android.R
import java.io.BufferedReader


//  Created by Graeme Costin on 24SEP20.
// The author disclaims copyright to this source code.  In place of
// a legal notice, here is a blessing:
//
//    May you do good and not evil.
//    May you find forgiveness for yourself and forgive others.
//    May you share freely, never taking more than you give.

// There will be one instance of this class for the currently selected Book.
// This instance will have a lifetime of the current book selection; its life
// will be terminated when the user selects a different Book to keyboard, at
// which time a new Book instance will be created for the newly selected Book.

class Book(
	val bkID: Int,
	val bibID: Int,
	val bkCode: String,
	val bkName: String,
	var chapRCr: Boolean,
	var numChap: Int,
	var currChap: Int
) {

	// The following variables and data structures have lifetimes of the Book object

	// Access to the KITDAO instance for kdb.sqlite access
	val dao = KITApp.dao
	val bibInst = KITApp.bibInst	// access to the instance of Bible for updating BibBooks[]

	// Properties of a Book instance defined in the primary constructor
//	var bkID: Int			bookID INTEGER
//	var bibID: Int			bibleID INTEGER - always 1 for KIT v1
//	var bkCode: String		bookCode TEXT
//	var bkName: String		bookName TEXT
//	var chapRCr: Boolean	chapRecsCreated INTEGER
//	var numChap: Int		numChaps INTEGER
//	var currChap: Int		currChapter INTEGER (the ID assigned by SQLite when the Chapter was created)

	var currChapOfst: Int = 0	// offset to the current Chapter in BibChaps[] array
	var chapInst: Chapter? = null	// instance in memory of the current Chapter

	// BibChaps array (for listing the Chapters so the user can choose one)

	data class BibChap(
		var chID: Int,        // chapterID INTEGER PRIMARY KEY assigned by SQLite when the Chapter was created
		var bibID: Int,        // bibleID INTEGER
		var bkID: Int,        // bookID INTEGER
		var chNum: Int,        // chapterNumber INTEGER
		var itRCr: Boolean,    // itemRecsCreated INTEGER
		var numVs: Int,        // numVerses INTEGER
		var numIt: Int,        // numItems INTEGER
		var curIt: Int        // currItem INTEGER
	) {
		override fun toString(): String {
			val ch_name = KITApp.res.getString(com.ccs.kitand.R.string.nm_chapter)
			val ps_name = KITApp.res.getString(com.ccs.kitand.R.string.nm_psalm)
			val d1String = (if (bkID == 19) ps_name else ch_name) + " " + chNum.toString()
			val d2String = (if (numVs >0) " (" + numVs.toString() + " verses)" else "")
			val displayString = d1String + d2String
			return displayString
		}
	}

	val BibChaps = ArrayList<BibChap>()

	// When the instance of Bible creates the instance for the current Book it supplies the values for
	// the currently selected book from the BibBooks array

	init {
		// A reference to this instance of the current Book needs to be saved in KITApp
		KITApp.bkInst = this

		// On the first time this Book has been selected the Chapter records must be created
		if (!chapRCr) {
			createChapterRecords(bkID, bibID, bkCode)
		}

		// Every time this Book is selected: The Chapters records in kdb.sqlite will have been
		// created at this point (either during this occasion or on a previous occasion),
		// so we set up the array BibChaps of Chapters by reading the records from kdb.sqlite.
		//
		// This array will last while this Book is the currently selected Book and will
		// be used whenever the user is allowed to select a Chapter; it will also be updated
		// when VerseItem records for this Chapter are created, and when the user chooses
		// a different Chapter to edit.
		// Its life will end when the user chooses a different Book to edit.

		dao.readChaptersRecs(bibID, this)
		// calls readChaptersRecs() in KITDAO.swift to read the kdb.sqlite database Books table
		// readChaptersRecs() calls appendChapterToArray() in this file for each ROW read from kdb.sqlite
		print("Chapter records for $bkName have been read from kdb.sqlite")
	}

	fun createChapterRecords(book: Int, bib: Int, code: String) {

		// Open kit_bookspec and read its data
		val res = KITApp.res
		val specStr = res.openRawResource(com.ccs.kitand.R.raw.kit_bookspec)
		val specRdr = BufferedReader(specStr.reader())
		val specTxt: String
		try {
			specTxt = specRdr.readText()
		} finally {
			specRdr.close()
		}

		val specLines = specTxt.split("\n").toTypedArray()

		// Find the line containing the String code
		var i = 0
		while (!specLines[i].contains(code)) {
			i = i + 1
		}

		// Process that line to create the Chapter records for this Book
		val bkStrs = specLines[i].split(", ").toTypedArray()
		val bkMList = bkStrs.toMutableList()
		bkMList.removeAt(1)	// we already have the Book three letter code
		bkMList.removeAt(0)	// we already have the Book ID
		numChap = bkMList.count()

		// Create a Chapters record in kdb.sqlite for each Chapter in this Book
		var chNum = 1	// Start at Chapter 1
		val currIt = 0	// No current VerseItem yet
		for (elem in bkMList) {
			var numIt = 0
			var elemTr = elem		// for some Psalms a preceding "A" will be removed
			if (elem.first() == 'A') {
				numIt = 1	// 1 for the Psalm ascription
				elemTr = elem.drop(1)	// remove the "A"
			}
			val numVs = elemTr.toInt()
			numIt = numIt + numVs	// for some Psalms numIt will include the ascription VerseItem
			if (dao.chaptersInsertRec(bib, book, chNum, false, numVs, numIt, currIt) ) {
				println("Book:createChapterRecords Created Chapter record for $bkName, chapter $chNum")
			}
			chNum = chNum + 1
		}
		// Update in-memory record of current Book to indicate that its Chapter records have been created
		chapRCr = true
		// numChap = numChap This was done when the count of elements in the chapters string was found

		// Update kdb.sqlite Books record of current Book to indicate that its Chapter records have been created,
		// the number of Chapters has been found, but there is not yet a current Chapter
		if (dao.booksUpdateRec(bibID, bkID, chapRCr, numChap, currChap) ) {
			print("Book:createChapterRecords updated the record for this Book")
		}

		// Update the entry in BibBooks[] for the current Book to show that its Chapter records have been created
		// and that its number of Chapters has been found
		bibInst.setBibBooksNumChap(numChap)
	}

	// dao.readChaptersRecs() calls appendChapterToArray() for each row it reads from the kdb.sqlite database

	fun appendChapterToArray(
		chapID: Int, bibID: Int, bookID: Int,
		chNum: Int, itRCr: Boolean, numVs: Int, numIt: Int, curIt: Int
	) {
		val chRec = Book.BibChap(chapID, bibID, bookID, chNum, itRCr, numVs, numIt, curIt)
		BibChaps.add(chRec)
	}


	// Find the offset in BibChaps[] to the element having ChapterID withID.
	// If out of range returns offset zero (first item in the array).

	fun offsetToBibChap(withID: Int) : Int {
		for (i in 0..numChap-1) {
			if (BibChaps[i].chID == withID) {
				return i
			}
		}
		return 0
	}

	// If, from kdb.sqlite, there is already a current Chapter for the current Book then go to it
	// Go to the current BibChap
	// This function is called by the ChooseChaptersActivity to find out which Chapter
	// in the current Book is the current Chapter, and to make the Book instance and
	// the Book record remember that selection.
	fun goCurrentChapter() {
		currChapOfst = offsetToBibChap(currChap)

		// allow any previous in-memory instance of Chapter to be garbage collected
		chapInst = null

		// create a Chapter instance for the current Chapter of the current Book
		// The initialisation of the instance of Chapter stores a reference in KITApp
		val chap = BibChaps[currChapOfst]
		chapInst = Chapter(chap.chID, chap.bibID, chap.bkID, chap.chNum, chap.itRCr, chap.numVs, chap.numIt, chap.curIt)
	}

	// When the user selects a Chapter from the list of Chapters it needs to be recorded as the
	// current Chapter and initialisation of data structures in a new Chapter instance must happen.

	fun setupCurrentChapter(chapOfst: Int) {
		val chap = BibChaps[chapOfst]
		currChap = chap.chID
		currChapOfst = chapOfst
		// update Book record in kdb.sqlite to show this current Chapter
		if (dao.booksUpdateRec(bibID, bkID, chapRCr, numChap, currChap) ) {
			println("The currChap for $bkName in kdb.sqlite was updated to $chap.chNum")
			}

		// allow any previous in-memory instance of Chapter to be garbage collected
		chapInst = null

		// create a Chapter instance for the current Chapter of the current Book
		// The initialisation of the instance of Chapter stores a reference in KITApp
		chapInst = Chapter(chap.chID, chap.bibID, chap.bkID, chap.chNum, chap.itRCr, chap.numVs, chap.numIt, chap.curIt)
	}

	// When the VerseItem records have been created for the current Chapter, the entry for that Chapter in
	// the Book's BibChaps[] array must be updated. Once itRCr is set true it will never go back to false
	// (the kdb.sqlite records are not going to be deleted) so no parameter is needed for that,
	// but parameters are needed for the number of Verses and number of Items in the Chapter.
	// This function is called from the current Chapter instance, createItemRecords()

	fun setBibChapsNums(numVs:Int, numIt:Int) {
		BibChaps[currChapOfst].itRCr = true
		BibChaps[currChapOfst].numVs = numVs
		BibChaps[currChapOfst].numIt = numIt
	}

}
