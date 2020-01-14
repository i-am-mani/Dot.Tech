package com.omega.dottech2k20

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.omega.dottech2k20.Adapters.NoticeItem
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.Utils
import com.omega.dottech2k20.models.Notice
import com.omega.dottech2k20.models.UserEventViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

	private lateinit var appBarConfiguration: AppBarConfiguration
	private lateinit var navController: NavController
	private lateinit var notices: ArrayList<Notice>
	private val TAG = javaClass.simpleName
	private lateinit var mViewModel: UserEventViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProviders.of(this).get(UserEventViewModel::class.java)
		setContentView(R.layout.activity_main)
		setNavigationBar()
		setNavigationController()
		setDestinationListener()
		setNavigationMenuItems()
		createNotificationChannel()
		subscribeToTopicAll()
		registerTokenToUserData()
		initStartUpDialog()
	}

	override fun onResume() {
		super.onResume()
		Utils.virtualClickHapticFeedback(navigation_bar)
		FirebaseAuth.getInstance().currentUser?.reload()
	}
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		super.onCreateOptionsMenu(menu)
		menuInflater.inflate(R.menu.action_bar_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		super.onOptionsItemSelected(item)
		when {
			item?.itemId == R.id.notices -> {
				showNoticesDialog()
				return true
			}
		}
		return false
	}

	private fun initStartUpDialog() {
		try {
			notices = intent.extras.getParcelableArrayList<Notice>("notices")
			showNoticesDialog()
		} catch (e: Exception) {
			Log.e(TAG, "Error occurred in displaying notices dialog: ", e)
		}
	}

	private fun showNoticesDialog() {
		if (::notices.isInitialized && notices.count() > 0) {
			val noticesItems = mutableListOf<NoticeItem>()
			for (notice in notices) {
				noticesItems.add(NoticeItem(this, notice))
			}

			initStartDialog(noticesItems)
		}
	}

	private fun initStartDialog(items: MutableList<NoticeItem>) {
		val dialog = Dialog(this).apply {
			setCanceledOnTouchOutside(true)

			setContentView(R.layout.dialog_start_up)
			window?.setLayout(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT
			)
			window?.setGravity(Gravity.TOP)
			window?.setBackgroundDrawableResource(android.R.color.transparent)

			val rv_notices = findViewById<RecyclerView>(R.id.rv_notices)
			val adapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
			val layoutManager =
				LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
			adapter.addAll(items)

			rv_notices.layoutManager =
				layoutManager
			LinearSnapHelper().attachToRecyclerView(rv_notices)
			rv_notices.adapter = adapter

			val next = findViewById<ImageButton>(R.id.imbtn_next)
			val prev = findViewById<ImageButton>(R.id.imbtn_previous)
			val close = findViewById<Button>(R.id.btn_close)
			next.setOnClickListener {
				val pos = layoutManager.findFirstCompletelyVisibleItemPosition()
				adapter.let {
					layoutManager.scrollToPosition((pos + 1) % adapter.itemCount)
				}
			}
			prev.setOnClickListener {
				val pos = layoutManager.findFirstCompletelyVisibleItemPosition()
				adapter.let {
					layoutManager.scrollToPosition(abs(pos - 1) % adapter.itemCount)
				}
			}
			close.setOnClickListener {
				this.dismiss()
			}

		}
		dialog.show()
	}

	/**
	 * For signed in users, Register the token to user's data (Notification Id).
	 *
	 * If the Instance Id isn't present in NotificationIDs of User, then the id is added to the list
	 * of notificationId.
	 */
	private fun registerTokenToUserData() {

		mViewModel.getUserProfile()?.observe(this, Observer { user ->
			if (user != null) {
				FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
					if (task.isSuccessful) {

						task.result?.token?.let { token ->

							val notificationIds = user.notificationIds

							// If token isn't present, add it to list and replace the notificationIds
							if (notificationIds.find { it == token } == null) {
								mViewModel.updateNotificationId(token)
								Log.d("Notification", "New User data $user")
							}
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

		val headerView = nav_view.getHeaderView(0)
		if (headerView != null) {
			nav_view.removeHeaderView(headerView)
		}
	}

	private fun setSignedNavigationMenu() {
		drawer_layout.closeDrawers()
		nav_view.menu.clear()
		nav_view.inflateMenu(R.menu.drawer_navigation_signed_user)

		mViewModel.getUserProfile()?.observe(this, Observer {
			if (it != null) {
				var navView = nav_view.getHeaderView(0)
				if (navView == null) {
					navView = nav_view.inflateHeaderView(R.layout.nav_header_main)
				}
				val name = navView.findViewById<TextView>(R.id.tv_nav_full_name)
				val email = navView.findViewById<TextView>(R.id.tv_nav_email)
				email.text = it.email
				name.text = it.fullName
			}
		})
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
				if (destination.id == R.id.eventsFragment || destination.id == R.id.profileFragment || destination.id == R.id.notificationsFragment) {
					navigation_bar.show(destination.id, true)
					navigation_bar.visibility = View.VISIBLE
					Log.d(TAG, "Showing Nav bar")
				} else {
					navigation_bar.visibility = View.GONE
					TransitionManager.beginDelayedTransition(root_main, ChangeBounds())
				}

				if (destination.id == R.id.signOutFragment) {
					val intent = Intent(this, SignOutActivity::class.java)
					startActivity(intent)
					finish()
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
			Utils.virtualClickHapticFeedback(navigation_bar)
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

}
