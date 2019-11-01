package com.omega.dottech2k20

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
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
import kotlinx.android.synthetic.main.content_main.view.*


class MainActivity : AppCompatActivity() {

	lateinit var appBarConfiguration: AppBarConfiguration
	lateinit var navController: NavController

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
		return NavigationUI.navigateUp(nav_host_fragment.findNavController(), drawer_layout)
				|| super.onSupportNavigateUp()
	}

	private fun setNavigationBar() {
		initBottomNavigationView()
		setSupportActionBar(toolbar)
		window.statusBarColor = Color.TRANSPARENT
		//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
		//        supportActionBar!!.setDisplayShowHomeEnabled(true)
	}

	private fun setNavigationController() {
		navController = nav_host_fragment.findNavController()
		appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)

		NavigationUI.setupWithNavController(nav_view, navController)
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
	}

	private fun setDestinationListener() {
		navController.addOnDestinationChangedListener { controller, destination, arguments ->
			run {
				if (destination.id != R.id.eventsFragment) {
					navigation_bar.visibility = View.INVISIBLE
				} else {
					navigation_bar.visibility = View.VISIBLE
				}
			}
		}
	}


	private fun initBottomNavigationView() {
		navigation_bar.add(MeowBottomNavigation.Model(1, R.drawable.ic_profile_user))
		navigation_bar.add(MeowBottomNavigation.Model(2, R.drawable.ic_events))
		navigation_bar.add(MeowBottomNavigation.Model(3, R.drawable.ic_notification_bell))

		navigation_bar.show(2, true)
		navigation_bar.setOnClickMenuListener {
			when (it.id) {
				1 -> {
					navController.navigate(R.id.eventsFragment)
				}

				2 -> {
					// todo Add navigation to profile
				}
				3 -> {
					// todo Add navigation to Notification
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
