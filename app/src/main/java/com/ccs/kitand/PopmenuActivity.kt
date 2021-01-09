package com.ccs.kitand
// No longer needed -- delete when lessons from it need no more reminders!!!
// Also then delete activity_popmenu.xml
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class PopmenuActivity : AppCompatActivity() {

	lateinit var lst_popmenu: ListView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_popmenu)

		// Get references to layout widgets
		lst_popmenu = findViewById(R.id.lst_popmenu)

		lst_popmenu.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
			chooseMenuAction(position)
		})

		getSupportActionBar()?.hide()

		val dm = DisplayMetrics()
		windowManager.defaultDisplay.getMetrics(dm)

		val width = dm.widthPixels
		val height = dm.heightPixels

		window.setLayout((width * 0.8).toInt(), (height * 0.4).toInt())

	}

	override fun onStart() {
		super.onStart()

		val popArrayAdapter = ArrayAdapter<VIMenu.VIMenuItem>(
			this,
			android.R.layout.simple_selectable_list_item,
			KITApp.chInst.curPoMenu?.VIMenuItems!!
		)
		lst_popmenu.setAdapter(popArrayAdapter)

	}
	// Called when the user chooses an action on the popover menu
	fun chooseMenuAction(position: Int) {

	}
}