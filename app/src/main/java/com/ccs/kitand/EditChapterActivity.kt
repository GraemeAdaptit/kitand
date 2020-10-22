package com.ccs.kitand

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EditChapterActivity : AppCompatActivity() {

	private lateinit var ch_name:String
	private lateinit var ps_name:String
	private lateinit var recyclerView: RecyclerView
	private lateinit var viewAdapter: VerseItemAdapter	//RecyclerView.Adapter<*>
	private lateinit var viewManager: RecyclerView.LayoutManager

	var currIt = 0		// Zero until one of the VerseItems is chosen for editing;
						// then it is the ItemID of the VerseItem that is the current one. (not needed?)
	var currItOfst = -1	// -1 until one of the VerseItems is chosen for editing;
						// then it is the offset into the BibItems[] array which equals
						// the offset into the list of cells in the RecyclerView.

	val dao = KITApp.dao	// Get access to KITDAO

	var suppActionBar: ActionBar? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_edit_chapter)

		// Get access to the SupportActionBar
		suppActionBar = getSupportActionBar()

		// Get references to layout widgets
		ch_name = KITApp.res.getString(R.string.nm_chapter)
		ps_name = KITApp.res.getString(R.string.nm_psalm)

		val result = KITApp.chInst.goCurrentItem()
		this.currItOfst = result
		viewManager = LinearLayoutManager(this)
		viewAdapter = VerseItemAdapter(KITApp.chInst.BibItems, this) as VerseItemAdapter
		KITApp.vItAda = viewAdapter as VerseItemAdapter

		recyclerView = findViewById<RecyclerView>(R.id.lv_verseitemlist).apply {
			// use this setting to improve performance if you know that changes
			// in content do not change the layout size of the RecyclerView
			setHasFixedSize(true)
			// use a linear layout manager
			layoutManager = viewManager
			// specify a viewAdapter
			adapter = viewAdapter
		}
		KITApp.recycV = recyclerView
	}

	override fun onStart() {
		super.onStart()
//		txt_ed_bibname.setText(KITApp.bibInst.bibName)
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
		val result = KITApp.chInst.goCurrentItem()
		this.currItOfst = result
		// Tell the RecyclerView to got to this row
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.getItemId()) {
			android.R.id.home -> onBackPressed()
		}
		return true
	}

	override fun onBackPressed() {
		goToChapters()
	}

	private fun goToChapters() {
		// Save the current VerseItem text if necessary
		saveCurrentItemText()
		// Go to the ChooseChapterActivity
		val i = Intent(this, ChooseChapterActivity::class.java)
		startActivity(i)
		finish()	// If user returns to EditChapterActivity it will be to a new instance of the activity
	}

	// Called by the custom VerseItem cell when the user taps inside the cell's editable text
	fun userTappedInTextOfCell(position: Int) {
		tableRowSelectedAt(position)
//		tableView(self.tableView, didSelectRowAt: IndexPath(row: tableRow, section: 0))
	}

	// NOT YET Called by RecyclerView when the user selects a row of the view
	fun tableRowSelectedAt(position: Int) {
		val chInst = KITApp.chInst
		// Save the text in the current BibItem before changing to the new one
//		chInst.saveCurrentBibItemText()

		// Go to the newly selected VerseItem
		val bibItem = chInst.getBibItem(position)

		// Set up the selected Item as the current VerseItem
		chInst.setupCurrentItem(bibItem.itID)
//		currIt = bibItem.itID
//		currItOfst = indexPath.row
		// Scroll to make this VerseItem visible
//		tableView.selectRow(at: IndexPath(row: currItOfst, section: 0), animated: true, scrollPosition: UITableView.ScrollPosition.middle)
//		let cell = tableView.cellForRow(at: IndexPath(row: currItOfst, section: 0)) as! UIVerseItemCell
//		cell.itText.becomeFirstResponder()
	}

	// Called by ??? when the RecyclerView wants to reuse the cell for another VerseItem
	// Save itText before actual reuse unless there are no changes to itText
	// TODO: check whether we need to implement a test of whether the text has been changed.

//	fun saveCellText(position:Int, textSrc:String) {
//		KITApp.chInst.copyAndSaveVItem(position, textSrc)
//	}

	// Called when another VerseItem cell is selected in order to save the current VerseItem text
	// before making another VerseItem the current one
	fun saveCurrentItemText () {
		viewAdapter.saveCurrentItemText()
	}
}
