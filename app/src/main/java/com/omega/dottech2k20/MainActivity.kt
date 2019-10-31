package com.omega.dottech2k20

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigationView()
        setSupportActionBar(toolbar)
        window.statusBarColor = Color.TRANSPARENT

//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)


        val navController = nav_host_fragment.findNavController()
        appBarConfiguration = AppBarConfiguration(navController.graph,drawer_layout)

        NavigationUI.setupWithNavController(nav_view, navController)
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)

    }

    private fun setupNavigationMenu(navController: NavController) {
        val sideNavView = findViewById<NavigationView>(R.id.nav_view)
        sideNavView?.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(nav_host_fragment.findNavController(),drawer_layout)
                || super.onSupportNavigateUp()
    }
    private fun initBottomNavigationView() {

        navigation_bar.add(MeowBottomNavigation.Model(1, R.drawable.ic_profile_user))
        navigation_bar.add(MeowBottomNavigation.Model(2, R.drawable.ic_events))
        navigation_bar.add(MeowBottomNavigation.Model(3, R.drawable.ic_notification_bell))

        navigation_bar.show(2,true)
    }
}
