package com.ccs.kitand

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class SetupActivity : AppCompatActivity() {

    lateinit var btn_go: Button
    lateinit var txt_bibname: EditText

    // Safe initialisations of the four Properties of the Bible record
    // These variables of SetupActivity are used when creating the Bible instance
    var bibID: Int = 1	// Bible ID - always 1 for KIT v1
    var bibName: String = "Bible"	// Bible name
    var bkRCr: Boolean = false	// true when the Books records for this Bible have been created
    var currBook: Int = 0	// current Book ID
    // Bible Book IDs are assigned by the Bible Societies as 1 to 39 OT and 41 to 67 NT)

    lateinit var bibInst: Bible


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setup)
        // Get references to layout widgets
        btn_go = findViewById(R.id.btn_go)
        txt_bibname = findViewById(R.id.txt_bibname)

        btn_go.setOnClickListener(View.OnClickListener {
            // Get the (possibly edited) Bible name from the EditText widget
            val bibName: String = txt_bibname.text.toString()
            // Save the Bible name into the Bible record in kdb.sqlite
            KITApp.dao.bibleUpdateName(bibName)
            // Create the instance of Bible
            bibInst = Bible(bibID, bibName, bkRCr, currBook)
            // Go to the ChooseBookActivity
            val i = Intent(this, ChooseBookActivity::class.java)
            startActivity(i)
            finish()
        })
        println("SetupActivity::onCreate()")
    }

    override fun onStart() {
        super.onStart()
		println("SetupActivity::onStart()")
    }

    override fun onResume() {
        super.onResume()
        println("SetupActivity::onResume()")
        // Read the single Bibles record from kdb.sqlite
        val cv = KITApp.dao.bibleGetRec()
        bibID = cv.getAsInteger("1")
        bibName = cv.getAsString("2")
        bkRCr = cv.getAsBoolean("3")
        currBook = cv.getAsInteger("4")
        // Put the bibleName into the EditText widget
        txt_bibname.setText(bibName)
    }
}