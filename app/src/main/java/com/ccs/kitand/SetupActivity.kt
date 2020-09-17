package com.ccs.kitand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class SetupActivity : AppCompatActivity() {

    lateinit var btn_go: Button
    lateinit var txt_bibname: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setup)
        // Get references to layout widgets
        btn_go = findViewById(R.id.btn_go)
        txt_bibname = findViewById(R.id.txt_bibname)

        btn_go.setOnClickListener(View.OnClickListener {
            println("Go button clicked")
            val bName: String = txt_bibname.text.toString()
            KITApp.dao.bibleUpdateName(bName)
        })

        println("SetupActivity::onCreate()")
    }

    override fun onStart() {
        super.onStart()
		println("SetupActivity::onStart()")
		// Get the Bible record from kdb.sqlite
	// Code to use elsewhere (probably in KITDAO)
//        val stream = getResources().openRawResource(R.raw.kit_booknames)
//        val reader = BufferedReader(stream.reader())
//        var content: String
//        try {
//            content = reader.readText()
//        } finally {
//            reader.close()
//        }
    }

    override fun onResume() {
        super.onResume()
        println("SetupActivity::onResume()")
        // Read the single Bibles record from kdb.sqlite
        val cv = KITApp.dao.bibleGetRec()
        val bibname: EditText = findViewById(R.id.txt_bibname)
        val bibleName: String = cv.getAsString(KITApp.dao.bibNameKey())
        bibname.setText(bibleName)
//        println(KITApp.greeting)
//        KITApp.greeting = "Here is the new greeting"
//        println(KITApp.greeting)
//        KITApp.dao.whoAmI()
//        val flsDir = getFilesDir()
//        println("Files directory = $flsDir")
    }
}