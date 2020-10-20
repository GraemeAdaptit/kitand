package com.ccs.kitand

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView


class VerseItemAdapter(
	BibItems: ArrayList<Chapter.BibItem>,
	editChapterActivity: EditChapterActivity
) :
	RecyclerView.Adapter<VerseItemAdapter.ListCell>() {

	// The VerseItemAdapter needs to keep up to date the offset to the
	// view holder (VerseItem) that is the currently active one.
	var currCellOfst: Int

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
		val buttonText = if (itemType == "Verse") itemType + " " + verseNo.toString() else itemType
		holder.popoverButton.setText(buttonText)
		holder.verseItemTxt.setText(KITApp.chInst.BibItems[position].itTxt)
		holder.verseItemTxt.setActivated(true)
		if (this.currCellOfst == position) {
			holder.verseItemTxt.requestFocus()
		}
//		else {
//			holder.verseItemTxt.setActivated(false)
//		}

		// Listeners go in here
		// VerseItem text editing started
		holder.verseItemTxt.setOnClickListener(View.OnClickListener() {
			// text editing has started
			// if current ListCell is dirty, copy and save its text
			saveCurrentItemText()
			// make the ListCell just tapped the current one
			currCellOfst = holder.getAdapterPosition()
			KITApp.chInst.setupCurrentItemFromRecyclerRow(currCellOfst)
		})
		// VerseItem text is changing
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
		})
	}

	// Return the size of your dataset (invoked by the layout manager)
	override fun getItemCount() : Int {
		val numItems = KITApp.chInst.BibItems.size
		return numItems
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
			}
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
	}

}
