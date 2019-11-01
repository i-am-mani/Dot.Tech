package com.omega.dottech2k20.Fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.omega.dottech2k20.Models.User
import com.omega.dottech2k20.Models.UserEventViewModel


import com.omega.dottech2k20.R
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_user_event.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
	val TAG = javaClass.simpleName

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		val viewModel = ViewModelProviders.of(this).get(UserEventViewModel::class.java)
		val userProfile = viewModel.getUserProfile()?.observe(this, Observer {
			Log.d(TAG, "user = $it")
			if(it!=null){
				updateProfileDetails(it)
			}
		})
	}

	private fun updateProfileDetails(user: User) {
		val (id, fullName, email, phone, events) = user
		tv_full_name.text = fullName
		tv_email.text = email
		tv_phone.text = phone
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_profile, container, false)
	}


}
