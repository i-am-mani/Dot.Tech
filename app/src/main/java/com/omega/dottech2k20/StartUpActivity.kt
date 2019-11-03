package com.omega.dottech2k20

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.omega.dottech2k20.Fragments.SplashScreenFragment
import com.omega.dottech2k20.Fragments.WelcomeScreenFragment
import com.omega.dottech2k20.Utils.Utils
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage

class StartUpActivity : AppCompatActivity() {
    /***
     * If App is opened for first time, then show longer version of splash screen, and proceed to OnBoarding.
     * Otherwise show shorter version of Splash screen and proceed directly to main app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up)
		supportActionBar?.hide()
		window.setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN
		)

		val sharedPref =
			getSharedPreferences(Utils.sharedPreferenceName, Context.MODE_PRIVATE)!!
		val isFirstTime = sharedPref.getBoolean("first-time", true)

		return if (isFirstTime) {
			sharedPref.edit().putBoolean("first-time", false).apply()
			goToSplashWelcomeScreenFragment()

		} else {
			goToSplashScreenFragment()
		}

	}

	private fun goToSplashWelcomeScreenFragment() {
		var transaction = supportFragmentManager.beginTransaction()
		transaction.replace(
			R.id.start_up_activity_fragment_container,
			WelcomeScreenFragment()
		)
		transaction.commit()
    }

	private fun goToSplashScreenFragment() {

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.start_up_activity_fragment_container,
            SplashScreenFragment.newInstance()
        )
        transaction.commit()
    }

    fun goToMainActivity() {
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToOnBoardingFragment() {
		var pages = getOnBoardingPages()
		val onBoardingFragment = PaperOnboardingFragment.newInstance(pages)
		onBoardingFragment.setOnRightOutListener {
			goToMainActivity()
		}
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
		fragmentTransaction.add(R.id.start_up_activity_fragment_container, onBoardingFragment)
        fragmentTransaction.commit()
    }

	private fun getOnBoardingPages(): ArrayList<PaperOnboardingPage> {
        val notificationPage = PaperOnboardingPage(
            "Notification",
            "Be updated with latest information on events",
            Color.parseColor("#CE93D8"),
            R.drawable.ic_notification_1,
            R.drawable.ic_notification_2
        )
        val signupPage = PaperOnboardingPage(
            "Sign-up",
            "Register now, and get started!",
            Color.parseColor("#F48FB1"),
            R.drawable.ic_register,
            R.drawable.ic_register
        )
        val browseEventsPage = PaperOnboardingPage(
            "Browse",
            "Go through Variety of Events",
            Color.parseColor("#BBDEFB"),
            R.drawable.ic_events_2,
            R.drawable.ic_events_2
        )
        val joinEventsPage = PaperOnboardingPage(
            "Join",
            "Participate in any Event on the fly",
            Color.parseColor("#D1C4E9"),
            R.drawable.ic_events_1,
            R.drawable.ic_events_1
        )

        var pages = arrayListOf<PaperOnboardingPage>(
            browseEventsPage,
            joinEventsPage,
            notificationPage,
            signupPage
        )
        return pages
    }

}
