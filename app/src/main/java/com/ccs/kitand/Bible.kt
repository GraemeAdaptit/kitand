package com.ccs.kitand

import android.content.ContentValues
import android.content.res.Resources
import java.io.BufferedReader
import java.io.InputStream


//  Bible.kt
//
//  Created by Graeme Costin on 2/SEP/20.
// The author disclaims copyright to this source code.  In place of
// a legal notice, here is a blessing:
//
//    May you do good and not evil.
//    May you find forgiveness for yourself and forgive others.
//    May you share freely, never taking more than you give.
//
// This source file deals with the class Bible of which one instance will be created.
// The initialisation of this single instance will
// 1. Open the kdb.sqlite database (creating the database on first launch)
// 2. On first launch create the Books records in the kdb.sqlite database
// 3. On every launch read the Books records from kdb.sqlite and set up the array bibBooks
//    whose life is for the duration of this run of KIT
// 4. Do the other initialisations needed to build the partial in-memory data for the
//    Bible -> curr Book -> curr Chapter -> curr VerseItem data structures.

class Bible (bID: Int, bName: String, bkRecCr: Boolean, currBk: Int) {

	// The following variables and data structures have lifetimes of the Bible object
	// which is also the lifetime of this run of the app

	// Access to the KITDAO instance for kdb.sqlite access
	val dao = KITApp.dao

	// MARK: Properties

	var bibID: Int = bID			// Bible ID - always 1 for KIT v1
	var bibName: String = bName		// Bible name
	var bkRCr: Boolean = bkRecCr	// true if the Books records for this Bible have been created
	var currBook: Int = currBk		// current Book ID (defined by the Bible Societies 1 to 39 OT and 41 to 67 NT)

	var currBookOfst = -1			// Offset in BibBooks[] to the current book 0 to 38 (OT) 39 to 65 (NT)
	lateinit var bookInst: Book		// instance in memory of the current Book

	// BibBooks array (for listing the Books so the user can choose one)
	data class BibBook(
		var bkID: Int,            // bookID INTEGER
		var bibID: Int,            // bibleID INTEGER
		var bkCode: String,        // bookCode TEXT
		var bkName: String,        // bookName TEXT
		var chapRCr: Boolean,    // chapRecsCreated INTEGER
		var numCh: Int,            // numChaps INTEGER
		var currChap: Int        // currChapter INTEGER

	)	{
		override fun toString(): String {
			val displayStr = bkName + (if (numCh > 0) "  " + numCh.toString() + " chapters" else "")
			return  displayStr
		}
	}

	val BibBooks = ArrayList<BibBook>()

	// Initialisation of the single instance of class Bible with an array of Books to select from
	//	???	appendBibBookToArray()  - called by dao.readBooksRecs()

	init {
		currBookOfst = if (currBook > 39) (currBook - 2) else (currBook - 1)
		if (!bkRCr) {
			// Create the 66 Book records for this Bible
			createBooksRecords(bID)
		}
		// Every launch: the Books records will have been created at this point,
		// so set up the array BibBooks by reading the 66 Books records from kdb.sqlite.
		// This array will last for the current launch of the app and will be used
		// whenever the user is allowed to select a book; it will also be updated
		// when Chapters records for a book are created, and when the user chooses
		// a different Book to edit.
		//
		// readBooksRecs() in KITDAO.kt reads the kdb.sqlite database Books table
		// and calls appendBibBookToArray() in this file for each ROW read from kdb.sqlite
		// appendBibBookToArray() builds the array BibBooks
		dao.readBooksRecs (this)
		KITApp.bibInst = this
	}

	// createBooksRecords creates the Books records for every Bible book from the text files in the
	// app's resources and stores these records in the database kdb.sqlite
	fun createBooksRecords(bID: Int) {

		// Open kit_bookspec and read its data
		val res: Resources = KITApp.res
		val specStr = res.openRawResource(R.raw.kit_bookspec)
		val specRdr = BufferedReader(specStr.reader())
		val specTxt: String
		try {
			specTxt = specRdr.readText()
		} finally {
			specRdr.close()
		}

		// Open kit_booknames and read its data
		val namesStr = res.openRawResource(R.raw.kit_booknames)
        val nameRdr = BufferedReader(namesStr.reader())
        val nameTxt: String
        try {
			nameTxt = nameRdr.readText()
        } finally {
			nameRdr.close()
        }

		val specLines = specTxt.split("\n").toTypedArray()
		val nameLines = nameTxt.split("\n").toTypedArray()
		val bookNames = mutableMapOf<Int, String>()
		// Make a look-up dictionary for book name given book ID number
		for (nameItem in nameLines) {
			if (!nameItem.isEmpty()) {
				val nmStrs = nameItem.split(", ").toTypedArray()
				val i = nmStrs[0].toInt()
				val n = nmStrs[1]
				bookNames[i] = n
			}
		}

		// Step through the lines of KIT_BooksSpec.txt, creating the Book objects and
		// getting the book names from the look-up dictionary made from KIT_BooksNames.txt
		for (spec in specLines) {
			// Ignore empty lines and line starting with #
			if (!spec.isEmpty() && (spec.first() != '#')) {
				// Create the Books record for this Book
				val bkStrs = spec.split(", ").toTypedArray()
				val bkID = bkStrs[0].toInt()
				val bibID = bID
				val bkCode = bkStrs[1]
				val bkN = bookNames[bkID]
				var bkName: String
				if (bkN != null) bkName = bkN else bkName = "Book"
				val chRCr = false
				val numCh = 0
				val currCh = 0
				println("BookID = $bkID, BibleID = $bibID, Book Code = $bkCode, BookName = $bkName, ChapRecsCreated is $chRCr, numChaps = $numCh, CurrentChap = $currCh")
				// Write Books record to kdb.sqlite
				if (dao.booksInsertRec(bkID, bibID, bkCode, bkName, chRCr, numCh, currCh)) {
					println("The Books record for $bkName was created")
				} else {
					println("The Books record for $bkName was not created")
				}
			}
		}

		// Update the in-memory Bible record to note that Books recs have been created
		bkRCr = true

		// Update the kdb.sqlite Bible record to note that Books recs have been created
		if (dao.bibleUpdateRecsCreated()) {
			println("bookRecsCreated in the Bible rec was set to true")
		} else {
			println("bookRecsCreated in the Bible rec was not set to true")
		}

	}

	// dao.readBooksRecs() calls appendBibBookToArray() for each row it reads from the kdb.sqlite database

	fun appendBibBookToArray (bkID:Int, bibID:Int, bkCode:String, bkName:String,
							chapRCr:Boolean, numCh:Int, currChap:Int) {
		val bkRec = BibBook(bkID, bibID, bkCode, bkName, chapRCr, numCh, currChap)
		BibBooks.add(bkRec)
	}
/*
// Deinitialise (delete) the instance of class Bible

	deinit {
		// Also delete the instance of the SQLite data access object
		dao = nil
		print("Bible instance has been deleted")
	}

// Refresh display of the Bible Books table on return to foreground

	func refreshUIAfterReturnToForeground () {
		// TODO: Check whether there anything to do here?
		print ("KIT user interface has been refreshed.")
	}

// If there is a current Book (as read from kdb.sqlite) then instantiate that Book.
	func goCurrentBook () {
		let book = BibBooks[currBookOfst]
		print("Going to \(book.bkName) as the current Book")

		// delete any previous in-memory instance of Book
		bookInst = nil

		// create a Book instance for the currently selected book
		bookInst = Book(book.bkID, book.bibID, book.bkCode, book.bkName, book.chapRCr, book.numCh, book.currChap, self, dao!)
		// Keep a reference in the AppDelegate
		appDelegate.bookInst = self.bookInst
		print("KIT has created an instance of class Book for the old current Book \(book.bkName)")
	}

// When the user selects a book from the UITableView of books it needs to be recorded as the
// current book and initialisation of data structures in a new Book instance must happen.
	func setupCurrentBook(_ book: BibBook) {
		print("Making \(book.bkName) the current Book")
		currBook = book.bkID
		currBookOfst = (currBook > 39 ? currBook - 2 : currBook - 1 )
		// update Bible record in kdb.sqlite to show this current book
		if dao!.bibleUpdateCurrBook(currBook) {
			print("The currBook in kdb.sqlite was updated to \(currBook)")
		} else {
			print("The currBook in kdb.sqlite was not updated to \(currBook)")
		}

		// delete any previous in-memory instance of Book
		bookInst = nil

		// create a Book instance for the currently selected book
		bookInst = Book(book.bkID, book.bibID, book.bkCode, book.bkName, book.chapRCr, book.numCh, book.currChap, self, dao!)
		// Keep a reference in the AppDelegate
		appDelegate.bookInst = self.bookInst
		print("KIT has created an instance of class Book for the new current Book \(book.bkName)")

	}

// When the Chapter records have been created for the current Book, the entry for that Book in
// the Bible's BibBooks[] array must be updated. Once chapRCr is set true it will never go back to false
// (the kdb.sqlite records are not going to be deleted) so no parameter is needed for that,
// but a parameter is needed for the number of Chapters in the Book.

	func setBibBooksNumChap(_ numChap: Int) {
		BibBooks[currBookOfst].chapRCr = true
		BibBooks[currBookOfst].numCh = numChap
	}
*/
}
