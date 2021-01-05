package com.ccs.kitand

//
//  VIMenu.kt
//  kitand
//
// The author disclaims copyright to this source code.  In place of
// a legal notice, here is a blessing:
//
//    May you do good and not evil.
//    May you find forgiveness for yourself and forgive others.
//    May you share freely, never taking more than you give.
//
//  Created by Graeme Costin on 5/1/2021.
//  Copyright © 2021 Costin Computing Services. All rights reserved.
//
//	VIMenu gathers the data necessary for populating a popover ListView when the user
//	taps the VerseItem label. The action of tapping a VerseItem label makes that VerseItem
//	the current one even if it were not before the user tapped its label.

class VIMenuItem  (
	var VIMenuLabel : String,	// Menu label displayed to users
	var VIMenuAction : String,	// Menu action to be done if chosen by user
	var VIMenuHLight : String	// Highlight colour B = blue (for normal), R = Red (for delete/dangerous)
) {
}

class VIMenu (curItOfst: Int)
{
	// Properties of a VIMenu instance (dummy values to avoid having optional variables)
	var VIType = "Verse"						// the type of the VerseItem this menu is for
	var numRows: Int = 0						// number of rows needed for the popover menu
	val VIMenuItems = ArrayList<VIMenuItem>()	// array of the menu items

//	let appDelegate = UIApplication.shared.delegate as! AppDelegate

	init {
		val chInst = KITApp.chInst
		val bibItem = chInst.BibItems[curItOfst]

		VIType = bibItem.itTyp
		val chNum = chInst.chNum
		when (VIType) {
		"Ascription" -> { 		// Ascriptions before verse 1 of some Psalms
			val viM1 = VIMenuItem("Delete Ascription", "delAsc", "R")
			VIMenuItems.add(viM1)
		}
		"Title" -> {            // Title for a Book
			val viMI1 = VIMenuItem("Create Heading After", "crHdAft", "B")
			VIMenuItems.add(viMI1)
			val viMI2 = VIMenuItem("Create Intro Title", "crInTit", "B")
			VIMenuItems.add(viMI2)
			val viMI3 = VIMenuItem("Delete Title", "delTitle", "R")
			VIMenuItems.add(viMI3)
		}
		"InTitle"-> {            // Title within Book introductory matter
			val viMI1 = VIMenuItem("Create Intro Paragraph", "crInPar", "B")
			VIMenuItems.add(viMI1)
			val viMI2 = VIMenuItem("Create Intro Heading", "crInHed", "B")
			VIMenuItems.add(viMI2)
			val viMI3 = VIMenuItem("Delete Intro Title", "delInTit", "R")
			VIMenuItems.add(viMI3)
		}
		"InSubj" -> {			// Subject heading within Book introductory matter
			val viMI1 = VIMenuItem("Create Intro Paragraph", "crInPar", "B")
			VIMenuItems.add(viMI1)
			val viMI2 = VIMenuItem("Delete Intro Subject", "delInSubj", "R")
			VIMenuItems.add(viMI2)
		}
		"InPara" ->	{			// Paragraph within Book introductory matter
			val viMI1 = VIMenuItem("Create Intro Paragraph", "crInPar", "B")
			VIMenuItems.add(viMI1)
			val viMI2 = VIMenuItem("Create Intro Heading", "crInHed", "B")
			VIMenuItems.add(viMI2)
			if ((bibItem.vsNum == 1) && (chNum == 1)) {
				val viMI3 = VIMenuItem("Create Title", "crTitle", "B")
				VIMenuItems.add(viMI3)
			}
			val viMI4 = VIMenuItem("Delete Intro Paragraph", "delInPar", "R")
			VIMenuItems.add(viMI4)
		}
		"Heading" -> {			// Heading/Subject Heading
			val viMI1 = VIMenuItem("Create Parallel Ref", "crPalRef", "B")
			VIMenuItems.add(viMI1)
			if ((bibItem.vsNum == 1) && (chNum == 1)) {
				val viMI2 = VIMenuItem("Create Title", "crTitle", "B")
				VIMenuItems.add(viMI2)
			}
			val viMI3 = VIMenuItem("Delete Heading", "delHead", "R")
			VIMenuItems.add(viMI3)
		}
		"Para" -> {				// Paragraph before a verse
			val viMI1 = VIMenuItem("Create Heading", "crHdAft", "B")
			VIMenuItems.add(viMI1)
			val viMI2 = VIMenuItem("Delete Paragraph", "delPara", "R")
			VIMenuItems.add(viMI2)
		}
		"ParaCont" -> {			// Paragraph within a verse
			val viMI1 = VIMenuItem("Delete Paragraph", "delPCon", "R")
			VIMenuItems.add(viMI1)
		}
		"ParlRef" -> {			// Parallel Reference
			val viMI1 = VIMenuItem("Delete Parallel Ref", "delPalRef", "R")
			VIMenuItems.add(viMI1)
		}
		"Verse" -> {            // Verse
			if ((chInst.bkID == 19) && (bibItem.vsNum == 1) && (!chInst.hasAscription)) {
				val viMI1 = VIMenuItem("Create Ascription", "crAsc", "R")
				VIMenuItems.add(viMI1)
			}
			if (bibItem.vsNum == 1) {
				if ((chNum == 1) && (!chInst.hasTitle)) {
					val viMI2 = VIMenuItem("Create Title", "crTitle", "B")
					VIMenuItems.add(viMI2)
				}
			}
			val viMI3 = VIMenuItem("Create Heading Before", "crHdBef", "B")
			VIMenuItems.add(viMI3)
			val viMI4 = VIMenuItem("Create Paragraph Before", "crParaBef", "B")
			VIMenuItems.add(viMI4)
			if (!bibItem.isBrg) {
				val viMI5 = VIMenuItem("Create Paragraph In", "crParaCont", "B")
				VIMenuItems.add(viMI5)
			}
			val isNxtVs = (chInst.BibItems[curItOfst + 1].itTyp == "Verse")
			if ((bibItem.vsNum != chInst.numVs) && isNxtVs) {
				val viMI6 = VIMenuItem("Bridge Next Verse", "brid", "R")
				VIMenuItems.add(viMI6)
			}
			if (bibItem.isBrg) {
				val viMI7 = VIMenuItem("Unbridge Last Verse", "unBrid", "R")
				VIMenuItems.add(viMI7)
			}
		}
		else -> {
				val viMI1 = VIMenuItem("***MENU ERROR***", "NOOP", "R")
				VIMenuItems.add(viMI1)
		}
		}
		numRows = VIMenuItems.size

	}
}