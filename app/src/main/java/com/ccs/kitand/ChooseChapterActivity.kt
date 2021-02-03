package com.ccs.kitand

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBar

class ChooseChapterActivity : AppCompatActivity() {

	// Boolean for whether to let the user choose a Chapter
	var letUserChooseChapter = false	// Will be set from bkInst.canChooseAnotherChapter

	// tableRow of the selected Chapter
	// chRow = -1 means that no Chapter has been selected yet
	// chRow is used when determining whether the user had chosen a different Chapter from before
	var chRow = -1

//	lateinit var txt_bibname: TextView
	lateinit var txt_ch_prompt: TextView
	lateinit var lst_chapters: ListView
	lateinit var ch_name: String
	lateinit var ps_name: String
	lateinit var bkInst: Book

	var suppActionBar: ActionBar? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_choosechapter)

		// Get access to the SupportActionBar
		suppActionBar = getSupportActionBar()

		// Provide a Back button
		suppActionBar?.setDisplayHomeAsUpEnabled(true)

		// Get references to layout widgets
//		txt_bibname = findViewById(R.id.txt_ch_bibname)
		txt_ch_prompt = findViewById(R.id.txt_ch_prompt)
		lst_chapters = findViewById(R.id.lst_chapters)
		ch_name = KITApp.res.getString(R.string.nm_chapter)
		ps_name = KITApp.res.getString(R.string.nm_psalm)

		lst_chapters.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
			chooseChapterAction(position)
		})

	}

	override fun onStart() {
		super.onStart()

		val bibName = KITApp.bibInst.bibName
		bkInst = KITApp.bkInst
		val actionBarTitle = "Key It  -  " + bibName
		if (suppActionBar != null) {
			suppActionBar?.setDisplayShowTitleEnabled(true)
			suppActionBar?.setTitle(actionBarTitle)
		}
		// Set flag for when user comes back to choose another chapter
		letUserChooseChapter = bkInst.canChooseAnotherChapter
		// Most launches will have a current Chapter and will go straight to it
		if (!letUserChooseChapter && bkInst.currChap > 0) {
			bkInst.goCurrentChapter()	// Creates an instance for the current Book (from kdb.sqlite)
			// If the user comes back to the Choose Book scene we need to let him choose again
			bkInst.canChooseAnotherChapter = true
			// Go to the EditChapterActivity
			val i = Intent(this, EditChapterActivity::class.java)
			startActivity(i)
//			finish()		// Keep ChooseChapterActivity in the Back Stack
		} else {

			// On first launch, and when user wants to choose another chapter,
			// do nothing and wait for the user to choose a Chapter.
//			txt_bibname.setText(KITApp.bibInst.bibName)
			val prompt =
				if (KITApp.bkInst.bkID == 19) "Choose " + ps_name else "Choose " + ch_name + " of " + KITApp.bkInst.bkName
			txt_ch_prompt.setText(prompt)
			val chapterArrayAdapter = ArrayAdapter<Book.BibChap>(
				this,
				android.R.layout.simple_selectable_list_item,
				KITApp.bkInst.BibChaps
			)
			lst_chapters.setAdapter(chapterArrayAdapter)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.getItemId()) {
			android.R.id.home -> onBackPressed()
		}
		return true
	}

	override fun onBackPressed() {
		goToBooks()
	}

	private fun goToBooks() {
		// Go to the ChooseBookActivity
		val i = Intent(this, ChooseBookActivity::class.java)
		startActivity(i)
		finish()	// If user returns to ChooseChapterActivity it will be to a new instance of the activity
	}

	fun chooseChapterAction(position:Int) {
		val chRowNew = position
		val diffChap = chRowNew != chRow
		chRow = chRowNew
		// Set up the selected Chapter as the current Chapter
		bkInst.setupCurrentChapter(position, diffChap)

		// If the user comes back to the Choose Chapter scene we need to let him choose again
		bkInst.canChooseAnotherChapter = true

		// Go to the EditChapterActivity
		val i = Intent(this, EditChapterActivity::class.java)
		startActivity(i)
//		finish()		// Keep ChooseChapterActivity in the Back Stack
	}
}