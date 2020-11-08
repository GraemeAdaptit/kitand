package com.ccs.kitand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.ActionBar

class ExportChapterActivity : AppCompatActivity() {

	private lateinit var ch_name:String
	private lateinit var ps_name:String
	lateinit var txt_USFM: TextView

	var suppActionBar: ActionBar? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_exportchapter)

		// Get access to the SupportActionBar
		suppActionBar = getSupportActionBar()

		// Provide a Back button
		suppActionBar?.setDisplayHomeAsUpEnabled(true)

		// Get references to layout widgets
		txt_USFM = findViewById(R.id.txt_usfm)

		// Get names for prompt string
		ch_name = KITApp.res.getString(R.string.nm_chapter)
		ps_name = KITApp.res.getString(R.string.nm_psalm)
	}

	override fun onStart() {
		super.onStart()
		val bibName = KITApp.bibInst.bibName
		val chNumStr = KITApp.chInst.chNum.toString()
		val prompt = if (KITApp.bkInst.bkID == 19)
			" " + ps_name + " " + chNumStr else
			" " + ch_name + " " + chNumStr + " of " + KITApp.bkInst.bkName
		val actionBarTitle = bibName + prompt
		if (suppActionBar != null) {
			suppActionBar?.setDisplayShowTitleEnabled(true)
			suppActionBar?.setTitle(actionBarTitle)
		}
	}

	override fun onResume() {
		super.onResume()

		// Generate the USFM text
		val USFMexp = KITApp.chInst.calcUSFMExportText()
		// Display it to the user
		txt_USFM.setText(USFMexp)
		// Save it into the current Chapter record of kdb.sqlite
		if (KITApp.chInst.saveUSFMText (KITApp.chInst.chID, USFMexp) ) {
			println("ExportChapterViewController:viewDidLoad saved USFM text to kdb.sqlite")
		} else {
			println("ExportChapterViewController:viewDidLoad save to kdb.sqlite FAILED")
		}

	}
}