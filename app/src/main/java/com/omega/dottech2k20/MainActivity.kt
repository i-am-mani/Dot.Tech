package com.omega.dottech2k20

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.Utils.AuthenticationUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

	lateinit var appBarConfiguration: AppBarConfiguration
	lateinit var navController: NavController
	val TAG = javaClass.simpleName

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		setNavigationBar()
		setNavigationController()
		setDestinationListener()
		setNavigationMenuItems()
		createNotificationChannel()
		subscribeToTopicAll()
		registerTokenToUserData()

	}

	/**
	 * For signed in users, Register the token to user's data (Notification Id).
	 *
	 * If the Instance Id isn't present in NotificationIDs of User, then the id is added to the list
	 * of notificationId.
	 */
	private fun registerTokenToUserData() {

		val viewModel = ViewModelProviders.of(this).get(UserEventViewModel::class.java)
		viewModel.getUserProfile()?.observe(this, Observer { user ->
			if (user != null) {
				FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
					if (task.isSuccessful) {

						task.result?.token?.let { token ->

							val notificationIds = user.notificationIds

							// If token isn't present, add it to list and replace the notificationIds
							if (notificationIds != null && notificationIds.find { it == token } == null) {
								val newNotificationIds = notificationIds.toMutableList()
								newNotificationIds.add(token)
								user.notificationIds = newNotificationIds
								Log.d("Notification", "New User data $user")
							} else if (notificationIds == null) {
								user.notificationIds = listOf(token)
							}

							viewModel.updateUserProfile(user)
							Log.d(TAG, "FirebaseInstanceId = $token")
						}
					}
				}
			}
		})

	}

	private fun subscribeToTopicAll() {
		FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener {
			if (it.isSuccessful) {
				Log.d(TAG, "User Subscribed to Topic successfully")
			} else {
				Log.d(TAG, "Failed to subscribe to topic")
			}
		}
	}

	private fun createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val name = getString(R.string.channel_name)
			val descriptionText = getString(R.string.channel_description)
			val importance = NotificationManager.IMPORTANCE_DEFAULT
			val channel =
				NotificationChannel(getString(R.string.channel_id), name, importance).apply {
					description = descriptionText
				}
			// Register the channel with the system
			val notificationManager: NotificationManager =
				getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

			notificationManager.createNotificationChannel(channel)
		}
	}


	fun setNavigationMenuItems() {
		val currentUser = AuthenticationUtils.currentUser
		if (currentUser == null) {
			setUnsignedNavigationMenu()
		} else {
			setSignedNavigationMenu()
		}
	}

	private fun setUnsignedNavigationMenu() {
		drawer_layout.closeDrawers()
		nav_view.menu.clear()
		nav_view.inflateMenu(R.menu.drawer_navigation_unsigned_menu)
	}

	private fun setSignedNavigationMenu() {
		drawer_layout.closeDrawers()
		nav_view.menu.clear()
		nav_view.inflateMenu(R.menu.drawer_navigation_signed_user)
	}

	override fun onSupportNavigateUp(): Boolean {
		return NavigationUI.navigateUp(navController, appBarConfiguration)
	}

	private fun setNavigationBar() {
		initBottomNavigationView()
		setSupportActionBar(toolbar)
	}

	private fun setNavigationController() {
		navController = nav_host_fragment.findNavController()
		appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.eventsFragment,
				R.id.profileFragment,
				R.id.notificationsFragment
			), drawer_layout
		)

		NavigationUI.setupWithNavController(nav_view, navController)
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
	}

	private fun setDestinationListener() {
		navController.addOnDestinationChangedListener { controller, destination, arguments ->
			run {
				if (destination.id == R.id.eventsFragment || destination.id == R.id.profileFragment || destination.id == R.id.notificationsFragment) {
					navigation_bar.show(destination.id, true)
					navigation_bar.visibility = View.VISIBLE
				} else {
					// Gone so that, the navi_bar won't consume additional space.
					navigation_bar.visibility = View.GONE
				}
			}
		}
	}


	private fun initBottomNavigationView() {
		navigation_bar.add(
			MeowBottomNavigation.Model(
				R.id.profileFragment,
				R.drawable.ic_profile_user
			)
		)
		navigation_bar.add(MeowBottomNavigation.Model(R.id.eventsFragment, R.drawable.ic_events))
		navigation_bar.add(
			MeowBottomNavigation.Model(
				R.id.notificationsFragment,
				R.drawable.ic_notification_bell
			)
		)

		navigation_bar.show(2, true)
		navigation_bar.setOnClickMenuListener {
			when (it.id) {
				R.id.profileFragment -> {
					Log.d(TAG, "Navigating To ProfileFragment")
					navController.navigate(R.id.profileFragment)
				}

				R.id.eventsFragment -> {
					navController.navigate(R.id.eventsFragment)
					Log.d(TAG, "Navigating To Events")
				}
				R.id.notificationsFragment -> {
					navController.navigate(R.id.notificationsFragment)
					Log.d(TAG, "Navigating To Notification")
				}
			}
		}
	}

	fun Activity.circularRevealedAtCenter(view: View) {
		val cx = (view.left + view.right) / 2
		val cy = (view.top + view.bottom) / 2
		val finalRadius = Math.max(view.width, view.height)

		if (view.isAttachedToWindow) {
			val anim =
				ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius.toFloat())
			view.visibility = View.VISIBLE
			view.setBackgroundColor(ContextCompat.getColor(this, R.color.IndicatorDotColor))
			anim.duration = 550
			anim.start()
		}
	}
}
