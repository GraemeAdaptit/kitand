package com.ccs.kitand

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ccs.kitand.databinding.ActivityMainBinding

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
    }
}