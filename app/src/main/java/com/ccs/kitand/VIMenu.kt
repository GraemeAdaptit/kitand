package com.ccs.kitand

import android.graphics.Paint
import android.graphics.Typeface

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

class VIMenu(curItOfst: Int)
{
	// Properties of a VIMenu instance (dummy values to avoid having optional variables)
	var VIType = "Verse"						// the type of the VerseItem this menu is for
	var numRows: Int = 0						// number of rows needed for the popover menu
	var menuLabelLength: Float = 50.0F			// Minimum length of the menu label in points
												// (for calculating popover menu width)

	data class VIMenuItem(
		var VIMenuLabel: String,    // Menu label displayed to users
		var VIMenuAction: String,    // Menu action to be done if chosen by user
		var VIMenuIcon: String        // C = Create, D = Delete, B = Bridge, U = Unbridge
	) {
		override fun toString(): String {
			val displayStr = VIMenuLabel
			return  displayStr
		}
	}

	val VIMenuItems = ArrayList<VIMenuItem>()	// array of the menu items

	init {
		val chInst = KITApp.chInst!!
		val bibItem = chInst.BibItems[curItOfst]

		VIType = bibItem.itTyp
		val chNum = chInst.chNum
		when (VIType) {
			"Ascription" -> {        // Ascriptions before verse 1 of some Psalms
				val viM1 = VIMenuItem("Ascription", "delAsc", "D")
				VIMenuItems.add(viM1)
			}
			"Title" -> {            // Title for a Book
				if ((bibItem.vsNum == 1) && (chNum == 1) && (!chInst.hasInTitle)) {
					val viMI1 = VIMenuItem("Intro Title", "crInTit", "C")
					VIMenuItems.add(viMI1)
				}
				val viMI2 = VIMenuItem("Heading After", "crHdAft", "C")
				VIMenuItems.add(viMI2)
				val viMI3 = VIMenuItem("Title", "delTitle", "D")
				VIMenuItems.add(viMI3)
			}
			"InTitle" -> {            // Title within Book introductory matter
				val viMI1 = VIMenuItem("Intro Paragraph", "crInPar", "C")
				VIMenuItems.add(viMI1)
				val viMI2 = VIMenuItem("Intro Heading", "crInHed", "C")
				VIMenuItems.add(viMI2)
				val viMI3 = VIMenuItem("Intro Title", "delInTit", "D")
				VIMenuItems.add(viMI3)
			}
			"InSubj" -> {            // Subject heading within Book introductory matter
				if ((bibItem.vsNum == 1) && (chNum == 1) && (!chInst.hasInTitle)) {
					val viMI1 = VIMenuItem("Intro Title", "crInTit", "C")
					VIMenuItems.add(viMI1)
				}
				val viMI2 = VIMenuItem("Intro Paragraph", "crInPar", "C")
				VIMenuItems.add(viMI2)
				val viMI3 = VIMenuItem("Intro Subject", "delInSubj", "D")
				VIMenuItems.add(viMI3)
			}
			"InPara" -> {            // Paragraph within Book introductory matter
				val viMI1 = VIMenuItem("Intro Paragraph", "crInPar", "C")
				VIMenuItems.add(viMI1)
				val viMI2 = VIMenuItem("Intro Heading", "crInHed", "C")
				VIMenuItems.add(viMI2)
				if ((bibItem.vsNum == 1) && (chNum == 1) && (!chInst.hasTitle)) {
					val viMI3 = VIMenuItem("Title", "crTitle", "C")
					VIMenuItems.add(viMI3)
				}
				val viMI4 = VIMenuItem("Intro Paragraph", "delInPar", "D")
				VIMenuItems.add(viMI4)
			}
			"Heading" -> {            // Heading/Subject Heading
				if ((bibItem.vsNum == 1) && (chNum == 1) && (!chInst.hasTitle)) {
					val viMI1 = VIMenuItem("Title", "crTitle", "C")
					VIMenuItems.add(viMI1)
				}
				val viMI2 = VIMenuItem("Parallel Ref", "crPalRef", "C")
				VIMenuItems.add(viMI2)
				val viMI3 = VIMenuItem("Heading", "delHead", "D")
				VIMenuItems.add(viMI3)
			}
			"Para" -> {                // Paragraph before a verse
				val viMI1 = VIMenuItem("Heading", "crHdAft", "C")
				VIMenuItems.add(viMI1)
				val viMI2 = VIMenuItem("Paragraph", "delPara", "D")
				VIMenuItems.add(viMI2)
			}
			"ParaCont" -> {            // Paragraph within a verse
				val viMI1 = VIMenuItem("Paragraph", "delPCon", "D")
				VIMenuItems.add(viMI1)
			}
			"VerseCont" -> {        // Verse continuation after paragraph break
				val viMI1 = VIMenuItem("Paragraph", "delVCon", "D")
				VIMenuItems.add(viMI1)
			}
			"ParlRef" -> {
				val viMI1 = VIMenuItem("Parallel Ref", "delPalRef", "D")
				VIMenuItems.add(viMI1)
			}
			"Verse" -> {            // Verse
				if ((chInst.bkID == 19) && (bibItem.vsNum == 1) && (!chInst.hasAscription)) {
					val viMI1 = VIMenuItem("Ascription", "crAsc", "C")
					VIMenuItems.add(viMI1)
				}
				if (bibItem.vsNum == 1) {
					if ((chNum == 1) && (!chInst.hasInTitle)) {
						val viMI2 = VIMenuItem("Intro Title", "crInTit", "C")
						VIMenuItems.add(viMI2)
					}
				}
				if (bibItem.vsNum == 1) {
					if ((chNum == 1) && (!chInst.hasTitle)) {
						val viMI3 = VIMenuItem("Title", "crTitle", "C")
						VIMenuItems.add(viMI3)
					}
				}
				if (curItOfst == 0 || ( (curItOfst > 0) && (chInst.BibItems[curItOfst - 1].itTyp != "Heading") ) ) {
					val viMI4 = VIMenuItem("Heading Before", "crHdBef", "C")
					VIMenuItems.add(viMI4)
				}
				if (curItOfst == 0 || ( (curItOfst > 0) && (chInst.BibItems[curItOfst - 1].itTyp != "Para") ) ) {
					val viMI5 = VIMenuItem("Paragraph Before", "crParaBef", "C")
					VIMenuItems.add(viMI5)
				}
				if (curItOfst == 0 || (bibItem.itTyp != "VerseCont") ) {
					if (!bibItem.isBrg) {
						val viMI6 = VIMenuItem("Paragraph In", "crParaCont", "C")
						VIMenuItems.add(viMI6)
					}
				}
				val viMI7 = VIMenuItem("Parallel Ref", "crPalRef", "C")
				VIMenuItems.add(viMI7)
				val brgPossible: Boolean
				if (bibItem.isBrg) {
					brgPossible = (bibItem.lvBrg < chInst.numVs)
				} else {
					brgPossible = (bibItem.vsNum < chInst.numVs)
				}
				if (brgPossible) {
					// GDLC 24AUG21 Don't allow verse to be bridged with a following bridge
					val nextVI = chInst.BibItems[curItOfst + 1]
					if (nextVI.itTyp == "Verse" && !nextVI.isBrg) {
						val viMI8 = VIMenuItem("Bridge Next Verse", "brid", "B")
						VIMenuItems.add(viMI8)
					}
				}
				if (bibItem.isBrg) {
					val viMI9 = VIMenuItem("Unbridge Last Verse", "unBrid", "U")
					VIMenuItems.add(viMI9)
				}
			}
		else -> {
				val viMI1 = VIMenuItem("***MENU ERROR***", "NOOP", "C")
				VIMenuItems.add(viMI1)
			}
		}
		numRows = VIMenuItems.size
		// Calculate max popover menu label width
		val paint = Paint()
		paint.setTextSize(18F)
		paint.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));
		paint.setStyle(Paint.Style.FILL)
		for (v in VIMenuItems) {
			val width = paint.measureText(v.VIMenuLabel)
			if (width > menuLabelLength) {menuLabelLength = width}
		}

	}
}
