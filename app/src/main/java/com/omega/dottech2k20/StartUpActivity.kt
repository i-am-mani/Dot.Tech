package com.omega.dottech2k20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager

class StartUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)
        goToSplashScreenFragment()
    }

    fun goToSplashScreenFragment() {
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
//        val intent = Intent(this, MainActivity::class.java)


        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.start_up_activity_fragment_container, SplashScreenFragment.newInstance())
        transaction.commit()
//
//        val goToNextActivity: () -> Unit = {
//            goToOnBoardingFragment()
//        }
//        Handler(Looper.getMainLooper()).postDelayed(goToNextActivity, 2000)
    }

}
