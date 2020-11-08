package com.ccs.kitand


//  Created by Graeme Costin on 25SEP20.
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

// The Chapter records in the database store the ID of the current VerseItem for each Chapter
// because there are over 31,000 VerseItems and so verse numbers are not enough, and updating
// a VerseItem record in the database is more efficiently done if the record is identified by
// just its ID rather than by a combination of Book, Chapter, and VerseItem.
// On the other hand, the user interface needs only the VerseItems for the current Chapter and,
// in addition, the user interface functions on both Android and iOS expect data to be supplied
// from

class Chapter(
	val chID:Int,		// chapterID INTEGER PRIMARY KEY
	val bibID:Int,		// bibleID INTEGER
	val bkID:Int,		// bookID INTEGER,
	val chNum:Int,		// chapterNumber INTEGER
	var itRCr:Boolean,	// itemRecsCreated INTEGER
	val numVs:Int,		// numVerses INTEGER
	var numIt:Int,		// numItems INTEGER
	var currIt:Int		// currItem INTEGER (the ID assigned by SQLite when the VerseItem was created)
) {
	// The following variables and data structures also have lifetimes of the Chapter instance

	val dao = KITApp.dao			// Access to the KITDAO instance for kdb.sqlite access
	val bibInst = KITApp.bibInst	// access to the instance of Bible for updating BibBooks[] (not needed?)
	val bkInst = KITApp.bkInst		// access to the instance for the current Book

	var USFMText:String = ""	// Property to hold the USFM text exported
	var currItOfst: Int = -1	// offset to current item in BibItems[] and row in the TableView

	// This struct and the BibItems array are used for letting the user select the
	// VerseItem to edit in the current Chapter of the current Book.

	data class BibItem (
		val itID: Int,		// itemID INTEGER PRIMARY KEY
		val chID: Int,		// chapterID INTEGER
		val vsNum: Int,		// verseNumber INTEGER
		var itTyp: String,	// itemType TEXT
		var itOrd: Int,		// itemOrder INTEGER
		var itTxt: String,	// itemText TEXT
		var intSeq: Int,	// intSeq INTEGER
		var isBrg: Boolean,	// isBridge INTEGER
		var lvBrg: Int		// last verse of bridge
	)

	val BibItems = ArrayList<BibItem>()

	// When the instance of current Book creates the instance for the current Chapter it supplies
	// the values for the currently selected Chapter from the BibChaps array

	init {
		// A reference to this instance of the current Chapter needs to be saved in KITApp
		KITApp.chInst = this

		// First time this Chapter has been selected the Item records must be created
		if (!itRCr) {
			createItemRecords()
		}

		// Every time this Chapter is selected: The VerseItems records in kdb.sqlite will have been
		// created at this point (either during this occasion or on a previous occasion),
		// so we set up the array BibItems of VerseItems by reading the records from kdb.sqlite.
		//
		// This array will last while this Chapter is the currently selected Chapter and will
		// be used whenever the user is allowed to select a VerseItem for editing;
		// it will also be updated when VerseItem records for this Chapter are created,
		// and when the user chooses a different VerseItem to edit.
		// Its life will end when the user chooses a different Chapter or Book to edit.

		// Calls readVerseItemsRecs() in KITDAO.swift to read the kdb.sqlite database VerseItems table
		// readVerseItemsRecs() calls appendItemToArray() in this file for each ROW read from kdb.sqlite
		dao.readVerseItemsRecs (this)
	}

	// Create a VerseItem record in kdb.sqlite for each VerseItem in this Chapter
	// If this is a Psalm and it has an ascription then numIt will be 1 greater than numVs.
	// For all other Psalms or Chapters numIt will equal numVs at this early stage of building
	// the app's data

	fun createItemRecords() {
		// If there is a Psalm ascription then create it first.
		if (this.numIt > numVs) {
			val vsNum = 1
			val itTyp = "Ascription"
			val itOrd = 99
			val itText = ""
			val intSeq = 0
			val isBrid = false
			val lstVsBrid = 0
			if (dao.verseItemsInsertRec (chID, vsNum, itTyp, itOrd, itText, intSeq, isBrid, lstVsBrid) ) {
				println("Chapter:createItemRecords Created Verse record for chap $chNum vs $vsNum")
			}
		}
		for (vsNum in 1..numVs) {
			val itTyp = "Verse"
			val itOrd = 100*vsNum
			val itText = ""
			val intSeq = 0
			val isBrid = false
			val lstVsBrid = 0
			if (dao.verseItemsInsertRec (chID, vsNum, itTyp, itOrd, itText, intSeq, isBrid, lstVsBrid) ) {
				println("Chapter:createItemRecords Created Verse record for chap $chNum vs $vsNum")
			}
		}
		// Update in-memory record of current Chapter to indicate that its VerseItem records have been created
		itRCr = true
		// Also update the BibChap struct to show itRCr true and numVs & numIt
		bkInst.setBibChapsNums(numVs, numIt)
		// Update Chapter record to show that VerseItems have been created
		if (dao.chaptersUpdateRec (chID, itRCr, numIt, currIt) ) {
//			println("Chapter:createItemRecords update Chapter record for chap $chNum succeeded")
		}
	}

	// dao.readVerseItemRecs() calls appendItemToArray() for each row it reads from the kdb.sqlite database

	fun appendItemToArray(itID:Int, chID:Int, vsNum:Int, itTyp:String, itOrd:Int, itTxt:String, intSeq:Int, isBrg:Boolean, lvBrg:Int) {
		val itRec = BibItem(itID, chID, vsNum, itTyp, itOrd, itTxt, intSeq, isBrg, lvBrg)
		BibItems.add(itRec)
	}

//	// Return the BibItem at an index (i.e. offset in BibItems or in VerseItemAdapter
//	// TODO: Check whether this is needed - delete if not needed
//	fun getBibItem(index:Int) : BibItem {
//		return BibItems[index]
//	}

	// Find the offset in BibItems[] to the element having VerseItemID withID
	// If out of range returns offset zero (first item in the array)

	fun offsetToBibItem(withID:Int) : Int {
		for (i in 0 until numIt) {
			if (BibItems[i].itID == withID) {
				return i
			}
		}
		return 0
	}


	// Go to the current BibItem
	// This function is called by the EditChapterActivity to find out which VerseItem
	// in the current Chapter is the current VerseItem (if one has already been made current).
	// If there is already a current VerseItem it goes to that one, but if not it makes the
	// first VerseItem the current one and makes the Chapter record remember that selection.
	//
	// Returns the current Item offset in BibItems[] array to the EditChapterActivity
	// because this equals the row number in the RecyclerView.
	// This functions sets both currIt and currItOfst to indicate the current VerseItem
	fun goCurrentItem() : Int {
		if (currIt == 0) {
			// Make the first VerseItem the current one
			currItOfst = 0		// Take first item in BibItems[] array
			currIt = BibItems[currItOfst].itID	// Get its itemID
		} else {
			// Already have the itemID of the current item so need to get
			// the offset into the BibItems[] array
			currItOfst = offsetToBibItem(currIt)
		}
		// Update the database Chapter record
		if (dao.chaptersUpdateRec (chID, itRCr, numIt, currIt) ) {
			println("Chapter:goCurrentItem updated $bkInst.bkName $chNum Chapter record")
		}
		return currItOfst
	}

	// Set up the new current BibItem given the VerseItem's ID
	// (as assigned by SQLite when the database's VerseItem record was created)
	// TODO: This function is not yet used - delete it?
	fun setupCurrentItemFromID(curIt:Int) {
		this.currIt = curIt
		this.currItOfst = offsetToBibItem(curIt)

		// Update the database Chapter record
		if (dao.chaptersUpdateRec (chID, itRCr, numIt, currIt) ) {
//			println("Chapter:setupCurrentItem updated $bkInst.bkName) $chNum) Chapter record")
		}
	}

	fun setupCurrentItemFromRecyclerRow(pos: Int) {
		currItOfst = pos
		currIt = BibItems[pos].itID
		// Update the database Chapter record
		if (dao.chaptersUpdateRec (chID, itRCr, numIt, currIt) ) {
//			println("Chapter:goCurrentItem updated $bkInst.bkName $chNum Chapter record")
		}
	}

	// Copy and save a VerseItem's text; parameters are
	// Offset of the VerseItem
	// Text of the VerseItem
	fun copyAndSaveVItem(ofSt:Int, text:String) {
		BibItems[ofSt].itTxt = text
		if (dao.itemsUpdateRecText (BibItems[ofSt].itID, BibItems[ofSt].itTxt) ) {
			println("Chapter:copyAndSaveVItem text of current item saved to kdb.sqlite")
		}
//		chDirty = true	// An item in this chapter has been edited (No longer used in UI)
	}

	fun calcUSFMExportText() : String {
		var USFM = "\\id " + bkInst.bkCode + " " + bibInst.bibName + "\n\\c " + chNum.toString()
		for (i in 0 until numIt) {
			var s: String
			var vn: String
			val item = BibItems[i]
			val tx: String = item.itTxt
			when (item.itTyp) {
				"Verse" -> {
					if (item.isBrg) {
						vn = item.vsNum.toString() + "-" + item.lvBrg.toString()
					} else {
						vn = item.vsNum.toString()
					}
					s = "\n\\v $vn $tx"
				}
				"VerseCont" -> s = "\n" + tx
				"Para" -> s = "\n\\p "
				"ParaCont" -> s = "\n\\p "
				"Heading" -> s = "\n\\s " + tx
				"ParlRef" -> s = "\n\\r " + tx
				"Title" -> s = "\n\\mt " + tx
				"InTitle" -> s = "\n\\imt " + tx
				"InSubj" -> s = "\n\\ims " + tx
				"InPara" -> s = "\n\\ip " + tx
				"DesTitle" -> s = "\n\\d " + tx
				"Ascription" -> s = "\n\\d " + tx
				else -> s = ""
			}
			USFM = USFM + s
		}
		return USFM
	}

	// Save the USFM text to the Chapter instance and to kdb.sqlite
	fun saveUSFMText (chID:Int, text:String) : Boolean {
		USFMText = text
		return dao.updateUSFMText (chID, text)
	}

}