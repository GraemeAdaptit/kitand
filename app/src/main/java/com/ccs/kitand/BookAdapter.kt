package com.ccs.kitand

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter (
	var BibBooks: ArrayList<Bible.BibBook>,
	var chBkAct: ChooseBookActivity
) : RecyclerView.Adapter<BookAdapter.BookCell>(){

	// Create new view holders (invoked by the layout manager)
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookAdapter.BookCell {
		// create a new view
		val itemView = LayoutInflater.from(parent.context)
			.inflate(R.layout.book_item, parent, false)
		// set the view's size, margins, paddings and layout parameters

		return BookCell(itemView)
	}

	override fun onBindViewHolder(holder: BookCell, position: Int) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element's data
		val bibBook = BibBooks[position]
		val bookName = bibBook.bkName
		holder.bookName.setText(bookName)
		if (bibBook.chapRCr) {
			// Set colour of text
			holder.bookName.setTextColor(Color.parseColor("#0000CD"))
			// Set info text
			val bookInfo = "Chap " + bibBook.currChap.toString() + " (" + bibBook.numCh.toString() + " ch)"
			holder.bookInfo.setText(bookInfo)
		}

		// Listeners for Book selected
		holder.bookName.setOnClickListener(View.OnClickListener {
			// A BookCell icon has been tapped
			val bookOfst = holder.getAdapterPosition()
			chBkAct.chooseBookAction(bookOfst)
		})
		holder.bookInfo.setOnClickListener(View.OnClickListener {
			// A PopupCell menu command has been tapped
			val bookOfst = holder.getAdapterPosition()
			chBkAct.chooseBookAction(bookOfst)
		})
	}

	override fun getItemCount(): Int {
		val numItems = BibBooks.size
		return numItems
	}

	inner class BookCell(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var bookName: TextView = itemView.findViewById(R.id.bookName)
		var bookInfo: TextView = itemView.findViewById(R.id.bookInfo)
	}
}