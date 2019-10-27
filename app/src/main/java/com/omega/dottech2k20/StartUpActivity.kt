package com.omega.dottech2k20

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.fragment.app.FragmentTransaction
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
        goToSplashScreenFragment()
    }

    fun goToSplashScreenFragment() {
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.start_up_activity_fragment_container,
            SplashScreenFragment.newInstance()
        )
        transaction.commit()
    }

    fun goToMainActivity() {
        val goToNextActivity: () -> Unit = {

        }
        Handler(Looper.getMainLooper()).postDelayed(goToNextActivity, 2000)
    }

    fun goToOnBoardingFragment() {
        var pages = getOnboardingPages()
        val onboardingFragment = PaperOnboardingFragment.newInstance(pages)
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.start_up_activity_fragment_container, onboardingFragment)
        fragmentTransaction.commit()
    }

    private fun getOnboardingPages(): ArrayList<PaperOnboardingPage> {
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
