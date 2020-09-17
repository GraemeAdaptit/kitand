package com.ccs.kitand

import android.content.ContentValues
import android.content.res.AssetManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.widget.EditText
import com.ccs.kitand.databinding.ActivityMainBinding
//import com.ccs.kitand.KITDAO
import java.io.BufferedReader
import java.io.File
import java.io.InputStream

class MainActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
//      binding = ActivityMainBinding.inflate(layoutInflater)
//      setContentView(binding.root)
//      // Create the KITDAO instance
//      val dao = KITDAO(this)
//      KITApp.dao = dao
        println("MainActivity::onCreate()")
    }

    override fun onStart() {
        super.onStart()
		println("onStart()")
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
        println("onResume()")
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