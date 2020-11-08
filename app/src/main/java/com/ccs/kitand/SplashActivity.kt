package com.ccs.kitand

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity()  {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_splash)
		// Create the KITDAO instance
		val dao = KITDAO(this)
		KITApp.dao = dao
		// Get access to the raw resource files
		KITApp.res = this.getResources()
		Handler().postDelayed({ // This method will be executed once the timer is over
			val i = Intent(this, SetupActivity::class.java)
			startActivity(i)
			finish()
		}, 2000)
	}
}