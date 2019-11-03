package com.omega.dottech2k20

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
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
					navigation_bar.visibility = View.INVISIBLE
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

		if(view.isAttachedToWindow) {
			val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius.toFloat())
			view.visibility = View.VISIBLE
			view.setBackgroundColor(ContextCompat.getColor(this, R.color.IndicatorDotColor))
			anim.duration = 550
			anim.start()
		}
	}
}
