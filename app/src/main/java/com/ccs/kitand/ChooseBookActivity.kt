package com.ccs.kitand

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
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

	lateinit var txt_bk_prompt: TextView
	lateinit var lst_booklist: RecyclerView
	lateinit var recyclerView: RecyclerView
	lateinit var viewAdapter: BookAdapter
	private lateinit var viewManager: RecyclerView.LayoutManager

	var suppActionBar: ActionBar? = null
	// By the time ChooseBookActivity is started the Bible instance will have been created
	var bInst: Bible = KITApp.bibInst

	//	// Scale factor for calculating widget sizes
//	var scale: Float = 0.0F
//	// Layout width
//	var layout_width = 0
	// Layout height for calculating scrolling offset
	var layout_height = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_choosebook)

		// Get access to the SupportActionBar
		suppActionBar = getSupportActionBar()

		// Get references to layout widgets
		txt_bk_prompt = findViewById(R.id.txt_bk_prompt)
		lst_booklist = findViewById(R.id.lst_books)
	}

	override fun onStart() {
		super.onStart()

		val bibName = KITApp.bibInst.bibName
		bInst = KITApp.bibInst

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
			// Dispose of ChooseBookActivity to reduce memory usage
			finish()
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
			recyclerView.getViewTreeObserver().addOnPreDrawListener(object :
				ViewTreeObserver.OnPreDrawListener {
				override fun onPreDraw(): Boolean {
					if (recyclerView.getChildCount() > 0) {
						// Remove the listener to avoid continually triggering this code - once is enough.
						recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
						// Get the height of the layout
						layout_height = recyclerView.getMeasuredHeight()
						(viewManager as LinearLayoutManager).scrollToPositionWithOffset(bInst.currBookOfst, layout_height/2)
						return true
					}
					return false
				}
			})
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
		// Dispose of ChooseBookActivity to reduce memory usage
		finish()
	}

}