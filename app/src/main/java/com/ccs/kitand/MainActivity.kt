package com.ccs.kitand

import android.content.res.AssetManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.ccs.kitand.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.File
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        println("onCreate()")
    }

    override fun onStart() {
        super.onStart()
        binding.greeting.text = "onStart()"
        println("onStart()")
        val stream = getResources().openRawResource(R.raw.kit_booknames)
        val reader = BufferedReader(stream.reader())
        var content: String
        try {
            content = reader.readText()
        } finally {
            reader.close()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.greeting.text = "onResume()"
        println("onResume()")
        binding.greeting.text = KITApp.greeting
        println(KITApp.greeting)
        KITApp.greeting = "Here is the new greeting"
        binding.greeting.text = KITApp.greeting
        println(KITApp.greeting)
        val tw = KITApp.du.twice(2)
        println ("Twice 2 = $tw")
        binding.greeting.text = "Twice 2 = $tw"
        KITApp.dao.whoAmI()
        val flsDir = getFilesDir()
        println("Files directory = $flsDir")
        KITApp.dao.start()
    }
}