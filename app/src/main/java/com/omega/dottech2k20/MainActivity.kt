package com.omega.dottech2k20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBottomNavigationView()
    }

    private fun initBottomNavigationView() {

        navigation_bar.add(MeowBottomNavigation.Model(1, R.drawable.ic_profile_user))
        navigation_bar.add(MeowBottomNavigation.Model(2, R.drawable.ic_events))
        navigation_bar.add(MeowBottomNavigation.Model(3, R.drawable.ic_notification_bell))

        navigation_bar.show(2,true)
    }
}
