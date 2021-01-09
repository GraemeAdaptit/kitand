package com.ccs.kitand

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.AdapterView
import android.widget.ArrayAdapter

class EditChapterActivity : AppCompatActivity() {

	private lateinit var txt_ched_prompt: TextView
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

		// Get widget and names for prompt string
		txt_ched_prompt = findViewById(R.id.txt_ched_prompt)
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
		KITApp.edChAct = this
//		// Ensure that the soft keyboard will appear
		// TODO: Find a better way - this doesn't work
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		val inflater: MenuInflater = menuInflater
		inflater.inflate(R.menu.editchaptermenu, menu)
		return true
	}

	override fun onStart() {
		super.onStart()
		val bibName = KITApp.bibInst.bibName
		val chNumStr = KITApp.chInst.chNum.toString()
		val prompt = if (KITApp.bkInst.bkID == 19)
			"Edit " + ps_name + " " + chNumStr else
			"Edit " + ch_name + " " + chNumStr + " of " + KITApp.bkInst.bkName
		txt_ched_prompt.setText(prompt)
		val actionBarTitle = "Key It  -  " + bibName
		if (suppActionBar != null) {
			suppActionBar?.setDisplayShowTitleEnabled(true)
			suppActionBar?.setTitle(actionBarTitle)
		}
	}

	override fun onResume() {
		super.onResume()
		val posn = KITApp.chInst.goCurrentItem()
		this.currItOfst = posn
		// NOTE: at the time that onResume() is called, the RecyclerView has not been fully set up
		// so attempting to show the correct VerseItem as selected will not work (and may crash).
		// Setting a listener for the point when RecyclerView is fully set up is an OK approach.
		recyclerView.getViewTreeObserver().addOnPreDrawListener(object : OnPreDrawListener {
			override fun onPreDraw(): Boolean {
				if (recyclerView.getChildCount() > 0) {
					// Remove the listener to avoid continually triggering this code - once is enough.
					recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
					recyclerView.layoutManager?.scrollToPosition(posn)
					viewAdapter.selectCurrItem(posn)
					return true
				}
				return false
			}
		})
	}

	// The Android system calls this function when KIT is going into the background
	override fun onStop() {
		saveCurrentItemText()
		super.onStop()
	}
	// The Android system's Action Bar calls this function when the user taps the Back button
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.getItemId()) {
			android.R.id.home -> onBackPressed()
			R.id.export -> goToExport()
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

	// Go to the ExportChapterActivity
	private fun goToExport() {
		// Save the current VerseItem text if necessary
		saveCurrentItemText()
		// Go to the ChooseChapterActivity
		val i = Intent(this, ExportChapterActivity::class.java)
		startActivity(i)
	}

	// Show popover menu; called from showPopoverMenu() in VerseItemAdapter
	fun showPopOverMenu(butn: Button) {
		val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val popupView = inflater.inflate(R.layout.activity_popmenu, null)
		val display = windowManager.defaultDisplay
		val size = Point()
		display.getSize(size)
		val dispW: Int = size.x
		val dispH: Int = size.y
		var locations = IntArray(2)
		butn.getLocationInWindow(locations)
		val butW = butn.getWidth()
		val butH = butn.getHeight()
// No longer needed
//		// Go to the PopmenuActivity
//		val i = Intent(this, PopmenuActivity::class.java)
//		startActivity(i)
		val numRows = KITApp.chInst.curPoMenu?.numRows as Int
		val popupHeight = numRows * 180
		val popupWin = PopupWindow(popupView, dispW - butW + 10, popupHeight)
		popupWin.setOutsideTouchable(true)

		val lst_popmenu = popupView.findViewById(R.id.lst_popmenu) as ListView

		popupWin.showAtLocation (
			KITApp.recycV, // Location to display popup window
			Gravity.NO_GRAVITY, // Position of layout to display popup
			butW - 10, // X offset
			locations[1] // Y offset
		)

		val popMenuArrayAdapter = ArrayAdapter<VIMenu.VIMenuItem>(
				this,
				android.R.layout.simple_selectable_list_item,
				KITApp.chInst.curPoMenu!!.VIMenuItems
		)
		lst_popmenu.setAdapter(popMenuArrayAdapter)
	}

	// Called when another VerseItem cell is selected in order to save the current VerseItem text
	// before making another VerseItem the current one
	private fun saveCurrentItemText() {
		viewAdapter.saveCurrentItemText()
	}
}
