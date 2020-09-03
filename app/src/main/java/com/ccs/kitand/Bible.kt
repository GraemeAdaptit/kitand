package com.ccs.kitand

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

class Bible {

	// MARK: Properties

	// Safe initialisations of the four Properties of the Bible record
	var bibID: Int = 1	// Bible ID - always 1 for KIT v1
	var bibName: String = "Bible"	// Bible name
	var bkRCr: Boolean = false	// true if the Books records for this Bible have been created
	var currBook: Int = 0	// current Book ID (defined by the Bible Societies 1 to 39 OT and 41 to 67 NT)

	var currBookOfst = -1	// Offset in BibBooks[] to the current book 0 to 38 (OT) 39 to 65 (NT)
//	var bookInst: Book?		// instance in memory of the current Book

}