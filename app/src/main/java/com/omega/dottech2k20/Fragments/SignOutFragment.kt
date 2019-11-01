package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils

class SignOutFragment : Fragment() {

	lateinit var activity: MainActivity
	val TAG: String = javaClass.simpleName

	override fun onAttach(context: Context) {
		super.onAttach(context)
		activity = context as MainActivity

	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		signOutUser()
		return inflater.inflate(R.layout.fragment_sign_out, container, false)
	}

	private fun signOutUser() {
		if (AuthenticationUtils.currentUser != null) {
			AuthenticationUtils.signOutUser()
			activity.setNavigationMenuItems()
		} else {
			Log.e(TAG, "SignOut: Attempt To Sign out when, user isn't signed in. ")
		}
	}


}
