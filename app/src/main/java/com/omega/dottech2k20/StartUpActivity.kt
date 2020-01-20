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

	private val TAG = "StartUpActivity"
	private val mFirestore = FirebaseFirestore.getInstance()
	private var notices: List<Notice>? = null
	private var isAnimationCompleted = false
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
		if (isAnimationCompleted && notices != null) {
			val intent = Intent(this, MainActivity::class.java)

			val bundle = Bundle()
			bundle.putParcelableArrayList("notices", ArrayList(notices))
			intent.putExtras(bundle)
			startActivity(intent)
			finish()
		}
	}

	/**
	 * must be called after completing animation, otherwise goToMainActivity won't trigger
	 * Activity Change.
	 */
	fun markAnimationCompleted() {
		isAnimationCompleted = true
		displayFetchingLoader()
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
		val browseEventsPage = PaperOnboardingPage(
			"Explore",
			"Browse through over 14 Events spanning across 3 days! \n\n (swipe right to continue)",
			Color.parseColor("#90CAF9"),
			R.drawable.illustration_explore,
			R.drawable.illustration_explore
		)

		val joinEventsPage = PaperOnboardingPage(
			"Participate",
			"Join or Leave any Event on fly!",
			Color.parseColor("#66BB6A"),
			R.drawable.illustration_participation,
			R.drawable.illustration_participation
		)

		val realTimePage = PaperOnboardingPage(
			"Blazing Fast",
			"Events, Participants, Notifications, and all the data is updated in Real-Time," +
					" available across multiple devices instantaneously!",
			Color.parseColor("#C5CAE9"),
			R.drawable.illustration_speed,
			R.drawable.illustration_speed
		)

		val notificationPage = PaperOnboardingPage(
			"Notifications",
			"Be updated with latest information on events, rounds and more",
			Color.parseColor("#9FA8DA"),
			R.drawable.illustration_notification,
			R.drawable.illustration_notification
		)

		val signupPage = PaperOnboardingPage(
			"Join Us!",
			"Register now, and get started! \n\n (swipe right to continue)",
			Color.parseColor("#E57373"),
			R.drawable.illustration_rocket_launch,
			R.drawable.illustration_rocket_launch
		)

		return arrayListOf(
			browseEventsPage,
			joinEventsPage,
			realTimePage,
			notificationPage,
			signupPage
		)
	}

}
