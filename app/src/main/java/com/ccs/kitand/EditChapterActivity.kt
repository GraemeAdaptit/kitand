package com.ccs.kitand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EditChapterActivity : AppCompatActivity() {
	private lateinit var txt_ed_bibname: TextView
	private lateinit var txt_ed_prompt: TextView
	private lateinit var ch_name:String
	private lateinit var ps_name:String
	private lateinit var recyclerView: RecyclerView
	private lateinit var viewAdapter: RecyclerView.Adapter<*>
	private lateinit var viewManager: RecyclerView.LayoutManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_edit_chapter)

		// Get references to layout widgets
		txt_ed_bibname = findViewById(R.id.txt_edbibname)
		txt_ed_prompt = findViewById(R.id.txt_edprompt)
		ch_name = KITApp.res.getString(R.string.nm_chapter)
		ps_name = KITApp.res.getString(R.string.nm_psalm)

		viewManager = LinearLayoutManager(this)
		viewAdapter = VerseItemAdapter(KITApp.chInst.BibItems)

		recyclerView = findViewById<RecyclerView>(R.id.lv_verseitemlist).apply {
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
		txt_ed_bibname.setText(KITApp.bibInst.bibName)
		val chNumStr = KITApp.chInst.chNum.toString()
		val prompt = if (KITApp.bkInst.bkID == 19)
			"Edit " + ps_name + " " + chNumStr else
			"Edit " + ch_name + " " + chNumStr + " of " + KITApp.bkInst.bkName
		txt_ed_prompt.setText(prompt)

	}


}