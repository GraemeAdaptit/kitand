package com.ccs.kitand

import android.content.Intent
import android.os.Bundle
import android.util.Half.toFloat
import android.view.*
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EditChapterActivity : AppCompatActivity() {

	private lateinit var txt_ched_prompt: TextView
	private lateinit var ch_name:String
	private lateinit var ps_name:String
	lateinit var recyclerView: RecyclerView
	lateinit var viewAdapter: VerseItemAdapter
	private lateinit var viewManager: RecyclerView.LayoutManager
	lateinit var edChAct: EditChapterActivity

	var currIt = 0		// Zero until one of the VerseItems is chosen for editing;
						// then it is the ItemID of the VerseItem that is the current one. (not needed?)
	var currItOfst = -1	// -1 until one of the VerseItems is chosen for editing;
						// then it is the offset into the BibItems[] array which equals
						// the offset into the list of cells in the RecyclerView.

	val dao = KITApp.dao	// Get access to KITDAO

	// Scale factor for calculating size of PopupWindows
	var scale: Float = 0.0F
	// Layout width for calculating positioning of PopupWindows
	var layout_width = 0

	var suppActionBar: ActionBar? = null

	// Properties of the EditChapterActivity instance related to popover menus
	var curPoMenu: VIMenu? = null	// instance in memory of the current popover menu
	private var popupWin: PopupWindow? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_edit_chapter)

		edChAct = this
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

		recyclerView = findViewById<RecyclerView>(R.id.lv_verseitemlist).apply {
			// use this setting to improve performance if you know that changes
			// in content do not change the layout size of the RecyclerView
			setHasFixedSize(true)
			// use a linear layout manager
			layoutManager = viewManager
			// specify a viewAdapter
			adapter = viewAdapter
		}

//		// Ensure that the soft keyboard will appear
		// TODO: Find a way that works!
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
					// Get the screen's density scale
					scale = resources.displayMetrics.density
					// Get the width of the layout
					layout_width = recyclerView.getMeasuredWidth()
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
		val locations = IntArray(2)
		butn.getLocationInWindow(locations)
		val butW = butn.getWidth()
//		val butH = butn.getHeight()
		curPoMenu = KITApp.chInst.curPoMenu
		var numRows = 0
		if (curPoMenu == null) numRows = 0 else numRows = curPoMenu!!.numRows
		val popupWidth = layout_width - butW + 10
		val pHeightIntdp = numRows.times(44)
		val popupHeight = (pHeightIntdp.toFloat() * scale + 0.5f).toInt()
		val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
		val popupView = inflater.inflate(R.layout.activity_popup, null)
		popupWin = PopupWindow(popupView, popupWidth, popupHeight, true)
		val layoutMgr = LinearLayoutManager(applicationContext)
		val popupMenu = popupView.findViewById<RecyclerView>(R.id.popmenu)
		popupMenu.apply  {
			// use this setting to improve performance if you know that changes
			// in content do not change the layout size of the RecyclerView
			setHasFixedSize(true)
			// use a linear layout manager
			layoutManager = layoutMgr
			// specify a viewAdapter
			adapter = PopupAdapter(curPoMenu!!, edChAct)
		}
		popupWin!!.setOutsideTouchable(true)

		popupWin!!.showAtLocation(
			recyclerView, // View for popup window to appear over
			Gravity.NO_GRAVITY, // How to bias the position of the popup window
			butW - 10, // X offset
			locations[1] // Y offset
		)
	}

	fun popMenuAction(pos: Int) {
		val popMenuCode = curPoMenu!!.VIMenuItems[pos].VIMenuAction
		KITApp.chInst.popMenuAction(popMenuCode, viewAdapter)
		popupWin!!.dismiss()
		// Refresh the RecyclerView of VerseItems
		// Replacing the content of the RecyclerView causes its current contents to be saved to the database,
		// but the database has already been updated correctly (for example, with an Ascription deleted) and
		// so every VerseItem that is at present in the RecyclerView is saved to its preceding VerseItem in
		// the database -- Verse 2 text goes to Verse 1, etc.!!
		// This Boolean is a hack to prevent this; but there must be a better way!
		viewAdapter.setIsRefreshingRecyclerView(true)
		recyclerView.setAdapter(null);
		recyclerView.setLayoutManager(null);
		recyclerView.setAdapter(viewAdapter);
		recyclerView.setLayoutManager(viewManager);
		viewAdapter.notifyDataSetChanged()
		viewAdapter.setIsRefreshingRecyclerView(false)
		// NOTE: at this time, the RecyclerView may not have been fully set up
		// so attempting to show the correct VerseItem as selected may not work (and may crash).
		// Setting a listener for the point when RecyclerView is fully set up is an OK approach.
		recyclerView.getViewTreeObserver().addOnPreDrawListener(object : OnPreDrawListener {
			override fun onPreDraw(): Boolean {
				if (recyclerView.getChildCount() > 0) {
					// Remove the listener to avoid continually triggering this code - once is enough.
					recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
					recyclerView.layoutManager?.scrollToPosition(currItOfst)
					viewAdapter.selectCurrItem(currItOfst)
					return true
				}
				return false
			}
		})
	}

	// Called when another VerseItem cell is selected in order to save the current VerseItem text
	// before making another VerseItem the current one
	private fun saveCurrentItemText() {
		viewAdapter.saveCurrentItemText()
	}
}
