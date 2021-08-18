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

//    lateinit var bibInst: Bible
// GDLC 12AUG21 No need for local var bInst


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_setup)
        // Get references to layout widgets
        btn_go = findViewById(R.id.btn_go)
        txt_bibname = findViewById(R.id.txt_bibname)

        btn_go.setOnClickListener(View.OnClickListener {
            goButtonAction()
        })
    }

    override fun onResume() {
        super.onResume()
        // Read the single Bibles record from kdb.sqlite
        val cv = KITApp.dao.bibleGetRec()
        bibID = cv.getAsInteger("1")
        bibName = cv.getAsString("2")
        bkRCr = cv.getAsBoolean("3")
        currBook = cv.getAsInteger("4")

        //	Once the user has dealt with the Setup scene, subsequent launches skip this step.
        //	Any future editing of the name of the Bible will be done in a separate scene.
		if (bkRCr) {
            // Create the instance of Bible and
            // ensure rest of app has access to the Bible instance
            KITApp.bibInst = Bible(bibID, bibName, bkRCr, currBook)
             // Go to the ChooseBookActivity
            val i = Intent(this, ChooseBookActivity::class.java)
            startActivity(i)
            finish()
        } else {
    			// Initialise the text field and wait for user to edit Bible name
                txt_bibname.setText(bibName)
		}
    }

    fun goButtonAction () {
        // Get the (possibly edited) Bible name from the EditText widget
        val bibName: String = txt_bibname.text.toString()
        // Save the Bible name into the Bible record in kdb.sqlite
        KITApp.dao.bibleUpdateName(bibName)
        // Create the instance of Bible and
        // ensure rest of app has access to the Bible instance
        KITApp.bibInst = Bible(bibID, bibName, bkRCr, currBook)
        // Go to the ChooseBookActivity
        val i = Intent(this, ChooseBookActivity::class.java)
        startActivity(i)
        finish()
    }
}