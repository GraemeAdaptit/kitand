package com.ccs.kitand

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates


class VerseItemAdapter(
	BibItems: ArrayList<Chapter.BibItem>,
	editChapterActivity: EditChapterActivity
) :
	RecyclerView.Adapter<VerseItemAdapter.ListCell>() {

	// The VerseItemAdapter needs to keep up to date the offset to the
	// view holder (VerseItem) that is the currently active one.
	var currCellOfst: Int	// = -1	// -1 means it hasn't been set yet

	init {
		this.currCellOfst = editChapterActivity.currItOfst
	}

	// Create new view holders (invoked by the layout manager)
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseItemAdapter.ListCell {
		// create a new view
		val itemView = LayoutInflater.from(parent.context)
								.inflate(R.layout.layout_item, parent, false)
		// set the view's size, margins, paddings and layout parameters

		return ListCell(itemView)
	}

	// Replace the contents of a view holder
	// (invoked by the layout manager as it populates the rows in the RecyclerView)
	override fun onBindViewHolder(holder: ListCell, position: Int) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element's data
		val BibItem = KITApp.chInst.BibItems[position]
		val itemType = BibItem.itTyp
		val verseNo = BibItem.vsNum
		// Customise the popover button title
		var buttonText = ""
		when (itemType) {
		"Title" -> buttonText = "Main Title"
		"Para", "ParaCont" -> buttonText = "Paragraph"
		"ParlRef" -> buttonText = "Parallel Ref"
		"VerseCont" -> buttonText = "Verse " + verseNo.toString() + " (cont)"
		"Verse" -> {
			if (BibItem.isBrg) {
				buttonText = "Verses " + verseNo.toString() + "-" + BibItem.lvBrg.toString()
			} else {
				buttonText = "Verse " + verseNo.toString()
			}
		}
		"InTitle" -> buttonText = "Intro Title"
		"InSubj" -> buttonText = "Intro Heading"
		"InPara" -> buttonText = "Intro Paragraph"
		else -> buttonText = itemType
		}
		holder.popoverButton.setText(buttonText)
		// Set the VerseItem text into the cell
		holder.verseItemTxt.setText(BibItem.itTxt)
		if (this.currCellOfst == position) {
			// If this is the current VerseItem turn on EditText for editing
			holder.setSelected(true)
		} else {
			// Otherwise disable editing of the EditText field
			holder.setSelected(false)
		}

		// Listeners go in here
		// Listener for VerseItem text editing started
		holder.verseItemTxt.setOnClickListener(View.OnClickListener {
			// A VerseItem EditText has been tapped
			val newPos = holder.getAdapterPosition()
			moveCurrCellToClickedCell(newPos)
		})
		// Listener for VerseItem text is changing
		holder.verseItemTxt.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//				TODO("Not yet implemented")
			}
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//				TODO("Not yet implemented")
			}
			override fun afterTextChanged(s: Editable) {
				// Set dirty flag for this VerseItem text
				holder.dirty = true
			}
		})
		// Listener for popover button
		holder.popoverButton.setOnClickListener(View.OnClickListener() {
			println("Popover button tapped")
			// If the button is not on the current VerseItem then change the current VerseItem to this one
			val newPos = holder.getAdapterPosition()
//			if (newPos != currCellOfst) {
				moveCurrCellToClickedCell(newPos)
				showPopoverMenu(it)
//			}
		})
	}

	// Return the size of your data set (invoked by the layout manager)
	override fun getItemCount() : Int {
		val numItems = KITApp.chInst.BibItems.size
		return numItems
	}

	// Replacing the content of the RecyclerView causes its current contents to be saved to the database,
	// but the database has already been updated correctly (for example, with an Ascription deleted) and
	// so every VerseItem that is at present in the RecyclerView is saved to its preceding VerseItem in
	// the database -- Verse 2 text goes to Verse 1, etc.!!
	// This Boolean is a hack to prevent this; but there must be a better way!
	var isRefreshingRecyclerView: Boolean = false

	fun setIsRefreshingRecyclerView(flag: Boolean) {
		isRefreshingRecyclerView = flag
	}
	// onViewRecycled() is called by the LayoutManager just before clearing data from a ListCell
	// such as when the recyclerView is scrolled and some cells go out of view.
	override fun onViewRecycled(holder: ListCell): Unit {
		if (!isRefreshingRecyclerView) {
			val pos = holder.getAdapterPosition()
			// Save VerseItem text from cell at pos
			val textSrc = holder.verseItemTxt.getText().toString()
			KITApp.chInst.copyAndSaveVItem(pos, textSrc)
		}
	}
	// Called when the current view holder is being changed to another one; it is necessary to save
	// the text in whichever is the current view holder
	fun saveCurrentItemText () {
		val currCell = KITApp.recycV.findViewHolderForAdapterPosition(currCellOfst)
		if (currCell != null) {
			val curCell = currCell as ListCell
			if (curCell.dirty) {
				val textSrc = curCell.verseItemTxt.getText().toString()
				KITApp.chInst.copyAndSaveVItem(currCellOfst, textSrc)
				curCell.dirty = false
				curCell.setSelected(false)
			}
		}
	}


	// Returns from the text of the current cell, the cursor position, the text before the cursor
	// and the text after the cursor.
	fun currTextSplit() : ContentValues {
		val cv = ContentValues()
		var cursPos = 0
		var textBefore = ""
		var textAfter = ""
		val currCell = KITApp.recycV.findViewHolderForAdapterPosition(currCellOfst) as ListCell
		assert (currCell != null)
		if (currCell != null) {
			val edText = currCell.verseItemTxt
			val txtChars = edText.getText().toString()
			cursPos = edText.getSelectionStart()
			if (cursPos >= 0 && cursPos < txtChars.length )  {
					textBefore = txtChars.subSequence(0, cursPos) as String
					textAfter = txtChars.subSequence(cursPos, txtChars.length) as String
				} else {
					cursPos = 0
					textBefore = ""
					textAfter = ""
				}
			} else {
				cursPos = 0
				textBefore = ""
				textAfter = ""
			}
		cv.put("1", cursPos)
		cv.put("2", textBefore)
		cv.put("3", textAfter)
		return cv
	}

	// Member function of VerseItemAdapter for making the clicked cell the current cell
	// Called by the onClickListener for verseItemTxt
	fun moveCurrCellToClickedCell(newPos: Int) {
		// Get start of selection in the new clicked cell
		val newCurrCell = KITApp.recycV.findViewHolderForAdapterPosition(newPos) as ListCell
		val edText = newCurrCell.verseItemTxt
		val cursPos = edText.getSelectionStart()
		// Save the current cell if necessary
		saveCurrentItemText()
		// make the ListCell just tapped the current one
		currCellOfst = newPos
		KITApp.chInst.setupCurrentItemFromRecyclerRow(newPos)
		// Enable the new current cell
		newCurrCell.setSelected(true)
		edText.setSelection(cursPos)
	}

	// Member function of VerseItemAdapter for showing the popup window for the tapped VerseItem
	// Called by the onClickListener for popoverButton
	fun showPopoverMenu(it: View) {
		println("About to show popover menu")
		val btn_popovr = it as Button
		val btnName = btn_popovr.getText()
		var locations = IntArray(2)
		btn_popovr.getLocationInWindow(locations)
		// If the current cell has been edited it must be saved
		saveCurrentItemText()
		KITApp.edChAct.showPopOverMenu(btn_popovr)
	}

	// If the current VerseItem is outside the RecyclerView (i.e. invisible) then nothing is done;
	// when the current VerseItem is scrolled into view onBindViewHolder() will show it as the current one.
	fun selectCurrItem(position: Int) {
		val verItCell = KITApp.recycV.findViewHolderForAdapterPosition(currCellOfst)
		if (verItCell != null) {
			val verseItemCell = verItCell as ListCell
			verseItemCell.setSelected(true)
		}
	}

	// RecyclerView.Adapter has a function notifyItemChanged(position) which can be used
	// if a ListCell contents needs to be changed

	// ListCell class provides a reference to each widget in a view holder
	// It also keeps track of whether that ListCell has been edited.
	inner class ListCell(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var popoverButton: Button = itemView.findViewById(R.id.btn_popover)
		var verseItemTxt: EditText = itemView.findViewById(R.id.txt_verseitem)

		// No editing has been done yet, so dirty = false
		var dirty = false

		fun setSelected(state: Boolean) {
			if (state == true) {
				itemView.setBackgroundColor(Color.parseColor("#777777"))
				verseItemTxt.setEnabled(true)
				verseItemTxt.setFocusable(true)
				verseItemTxt.setFocusableInTouchMode(true)
				verseItemTxt.requestFocus()
			} else {
				itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
				verseItemTxt.setFocusableInTouchMode(false)
			}
		}
	}
}
