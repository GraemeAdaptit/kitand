package com.ccs.kitand

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ccs.kitand.Bible.BibBook


class ChooseBookActivity : AppCompatActivity()  {

	// Boolean for whether to let the user choose a Book
	var letUserChooseBook = false	// Will be set from bibInst.canChooseAnotherBook

	lateinit var txt_bibname: TextView
	lateinit var txt_bk_prompt: TextView
	lateinit var lst_booklist: RecyclerView
	lateinit var recyclerView: RecyclerView
	lateinit var viewAdapter: BookAdapter
	private lateinit var viewManager: RecyclerView.LayoutManager

	// tableRow of the selected Book
	var bkRow = 0		// Is this needed here? Remove if never used.

	var suppActionBar: ActionBar? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_choosebook)

		// Get access to the SupportActionBar
		suppActionBar = getSupportActionBar()

		// Get references to layout widgets
		txt_bk_prompt = findViewById(R.id.txt_bk_prompt)
		lst_booklist = findViewById(R.id.lst_books)

//		viewManager = LinearLayoutManager(this)
//		viewAdapter = BookAdapter(KITApp.bibInst.BibBooks, this) as BookAdapter
//		recyclerView = findViewById<RecyclerView>(R.id.lst_books).apply {
//			// use this setting to improve performance if you know that changes
//			// in content do not change the layout size of the RecyclerView
//			setHasFixedSize(true)
//			// use a linear layout manager
//			layoutManager = viewManager
//			// specify a viewAdapter
//			adapter = viewAdapter
//		}
	}

	override fun onStart() {
		super.onStart()

		val bibName = KITApp.bibInst.bibName
		val bInst = KITApp.bibInst

		val actionBarTitle = "Key It  -  " + bibName
		if (suppActionBar != null) {
			suppActionBar?.setDisplayShowTitleEnabled(true)
			suppActionBar?.setTitle(actionBarTitle)
		}
		// Most launches will have a current Book and will go straight to it
		letUserChooseBook = bInst.canChooseAnotherBook
		if (!letUserChooseBook && bInst.currBk > 0) {
			bInst.goCurrentBook()	// Creates an instance for the current Book (from kdb.sqlite)
			// If the user comes back to ChooseBookActivity we need to let him choose again
			bInst.canChooseAnotherBook = true
			// Go to the ChooseChapterActivity
			val i = Intent(this, ChooseChapterActivity::class.java)
			startActivity(i)
//			finish()	// Keep ChooseBookActivity in the Back Stack
		} else {
			// On first launch, and when user wants to choose another book,
			// set up the Books list and wait for the user to choose a Book.
			txt_bk_prompt.setText("Choose Book")
			viewManager = LinearLayoutManager(this)
			viewAdapter = BookAdapter(KITApp.bibInst.BibBooks, this) as BookAdapter
			recyclerView = findViewById<RecyclerView>(R.id.lst_books).apply {
				// use this setting to improve performance if you know that changes
				// in content do not change the layout size of the RecyclerView
				setHasFixedSize(true)
				// use a linear layout manager
				layoutManager = viewManager
				// specify a viewAdapter
				adapter = viewAdapter
			}
		}
	}

	fun chooseBookAction(position: Int) {
		val bInst = KITApp.bibInst
		val selectedBook = bInst.BibBooks[position]
		// Set up the selected Book as the current Book (this updates kdb.sqlite with the currBook)
		bInst.setupCurrentBook(selectedBook)

		// Current Book is selected so go to ChooseChapterActivity
		// If the user comes back to the Choose Book scene we need to let him choose again
		bInst.canChooseAnotherBook = true
		// Go to the ChooseChapterActivity
		val i = Intent(this, ChooseChapterActivity::class.java)
		startActivity(i)
//		finish()	// Keep ChooseBookActivity in the Back Stack
	}

}