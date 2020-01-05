package com.omega.dottech2k20

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SignOutActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_sign_out)
		actionBar?.hide()

		Handler(mainLooper).postDelayed({
			val mainAppIntent = Intent(this, StartUpActivity::class.java)
			startActivity(mainAppIntent)
			finish()
		}, 1000)
	}

}
