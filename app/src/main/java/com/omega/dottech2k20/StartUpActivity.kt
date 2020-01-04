package com.omega.dottech2k20

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.FirebaseFirestore
import com.omega.dottech2k20.Fragments.SplashScreenFragment
import com.omega.dottech2k20.Fragments.WelcomeScreenFragment
import com.omega.dottech2k20.Utils.Utils
import com.omega.dottech2k20.models.Notice
import com.ramotion.paperonboarding.PaperOnboardingFragment
import com.ramotion.paperonboarding.PaperOnboardingPage
import kotlinx.android.synthetic.main.activity_start_up.*

class StartUpActivity : AppCompatActivity() {

	val TAG = "StartUpActivity"
	val mFirestore = FirebaseFirestore.getInstance()
	var notices: List<Notice>? = null
	var isAnimationCompleted = false
	/***
	 * If App is opened for first time, then show longer version of splash screen, and proceed to OnBoarding.
	 * Otherwise show shorter version of Splash screen and proceed directly to main app.
	 */
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		fetchNotices()
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

	private fun fetchNotices() {
		val query = mFirestore.collection("Notices")
		// Fetch data from server, avoid cache. As it would also work to check if internet is working.
		query.get().addOnCompleteListener {
			if (it.isSuccessful) {
				val result = it.result

				if (result != null) {
					notices = result.toObjects(Notice::class.java)
					Log.d(TAG, "Notices = $notices")
					goToMainActivity()
				} else {
					Log.e(TAG, "Failed to fetch notices: ", it.exception)
				}
			}
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


	/**
	 *  Switch to MainActivity, Passing Notices to MainActivity
	 *
	 *	Going to main activity has 2 Cases:
	 *
	 *	(1) - Fetched data arrives even before splash screen animation has ended
	 *	To prevent this use isAnimationCompleted. If it's false then do not switch activity.
	 *
	 *	(2) - Fetched Data arrives after animation has ended, in this case simply check if the
	 *	notices list (notices) isn't null. Which would imply data was fetched.
	 *
	 *	Since there are only 2 exit points, either after animation or after fetching the data
	 *
	 *	This method is bound to work.
	 */
	fun goToMainActivity() {
		displayFetchingLoader()
		if (isAnimationCompleted && notices != null) {
			val intent = Intent(this, MainActivity::class.java)

			val bundle = Bundle()
			bundle.putParcelableArrayList("notices", ArrayList(notices))
			intent.putExtras(bundle)
			startActivity(intent)
			finish()
		}
	}

	fun markAnimationCompleted() {
		isAnimationCompleted = true
	}

	fun displayFetchingLoader() {
		pb_startup.visibility = View.VISIBLE
		tv_no_connection.visibility = View.VISIBLE
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
