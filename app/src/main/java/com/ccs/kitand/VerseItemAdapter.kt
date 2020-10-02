package com.ccs.kitand

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class VerseItemAdapter(BibItems: ArrayList<Chapter.BibItem>) :
	RecyclerView.Adapter<VerseItemAdapter.MyViewHolder>() {

	// Provide a reference to the views for each data item
	class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var popoverButton: Button = itemView.findViewById(R.id.btn_popover)
		var verseItemTxt: EditText = itemView.findViewById(R.id.txt_verseitem)

	}


	// Create new views (invoked by the layout manager)
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerseItemAdapter.MyViewHolder {
		// create a new view
		val itemView = LayoutInflater.from(parent.context)
								.inflate(R.layout.layout_item, parent, false)
		// set the view's size, margins, paddings and layout parameters
//		itemView.layoutParams.height = 150

		return MyViewHolder(itemView)
	}

	// Replace the contents of a view (invoked by the layout manager)
	override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		val BibItem = KITApp.chInst.BibItems[position]
		val itemType = BibItem.itTyp
		val verseNo = BibItem.vsNum
		val buttonText = if (itemType == "Verse") itemType + " " + verseNo.toString() else itemType
		holder.popoverButton.setText(buttonText)
		holder.verseItemTxt.setText(KITApp.chInst.BibItems[position].itTxt)
	}

	// Return the size of your dataset (invoked by the layout manager)
	override fun getItemCount() : Int {
		val numItems = KITApp.chInst.BibItems.size
		return numItems
	}
}