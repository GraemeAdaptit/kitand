package com.ccs.kitand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PopupActivity : AppCompatActivity() {

	private lateinit var recyclerView: RecyclerView
	private lateinit var viewAdapter: RecyclerView.Adapter<*>
	private lateinit var viewManager: RecyclerView.LayoutManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_popup)

		viewManager = LinearLayoutManager(this)
		viewAdapter = PopupAdapter(KITApp.edChAct.curPoMenu!!) as RecyclerView.Adapter<*>

		recyclerView = findViewById<RecyclerView>(R.id.popmenu).apply {
			// use this setting to improve performance if you know that changes
			// in content do not change the layout size of the RecyclerView
			setHasFixedSize(true)
			// use a linear layout manager
			layoutManager = viewManager
			// specify a viewAdapter
			adapter = viewAdapter
		}
	}

	override fun onStart() {
		super.onStart()
	}

	override fun onResume() {
		super.onResume()

	}

}