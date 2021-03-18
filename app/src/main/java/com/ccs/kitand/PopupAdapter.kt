package com.ccs.kitand

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PopupAdapter (var curPoMenu: VIMenu)
	: RecyclerView.Adapter<PopupAdapter.PopupCell>(){

	// Create new view holders (invoked by the layout manager)
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupAdapter.PopupCell {
		// create a new view
		val itemView = LayoutInflater.from(parent.context)
			.inflate(R.layout.popup_item, parent, false)
		// set the view's size, margins, paddings and layout parameters

		return PopupCell(itemView)
	}

	override fun onBindViewHolder(holder: PopupCell, position: Int) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element's data
		val menuText = curPoMenu.VIMenuItems[position].VIMenuLabel
		holder.menu_cmd.setText(menuText)
		when (curPoMenu.VIMenuItems[position].VIMenuIcon) {
			"C" -> holder.popup_icon.setImageResource(R.drawable.create_pubitem)
			"D" -> holder.popup_icon.setImageResource(R.drawable.delete_pubitem)
			"B" -> holder.popup_icon.setImageResource(R.drawable.bridge_pubitem)
			"U" -> holder.popup_icon.setImageResource(R.drawable.unbridge_pubitem)
		}
	}

	override fun getItemCount(): Int {
		val numItems = curPoMenu.VIMenuItems.size
		return numItems
	}

	// PopupCell class provides a reference to each widget in a view holder
	inner class PopupCell(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var popup_icon: ImageView = itemView.findViewById(R.id.popup_icon)
		var menu_cmd: EditText = itemView.findViewById(R.id.menu_cmd)

	}

}