package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.OnFlingListener
import com.omega.dottech2k20.Adapters.UserEventItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.Models.User
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.BinaryDialog
import com.omega.dottech2k20.Utils.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {
	val TAG = javaClass.simpleName
	lateinit var mViewModel: UserEventViewModel
	var mAdapter = GroupAdapter<GroupieViewHolder>()
	lateinit var mActivity: MainActivity

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity

	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(UserEventViewModel::class.java)
		val profile = mViewModel.getUserProfile()

		val currentUser = AuthenticationUtils.currentUser
		if (currentUser != null) {

			profile?.observe(this, Observer {
				if (it != null) {
					updateProfileDetails(it)
				}
			})
			mViewModel.getUserEvent()?.observe(this, Observer {
				Log.d("ViewModel", "Event list = ${it?.count() ?: 0}")
				if (it != null) {
					updateUserEvents(it)
				}
			})
		}
	}

	private fun updateUserEvents(events: List<Event>) {
		rv_user_events.layoutManager =
			LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
		val eventItems = getEventItems(events)
		mAdapter.update(eventItems)
		rv_user_events.adapter = mAdapter
	}

	private fun getEventItems(events: List<Event>): List<UserEventItem> {
		val list = arrayListOf<UserEventItem>()
		for (event in events) {
			list.add(UserEventItem(event, this::leaveEvent, this::viewDetails))
		}
		return list
	}

	private fun leaveEvent(event: Event) {
		mViewModel.unjoinEvents(event)
	}

	private fun viewDetails(events: Event) {
		//todo Implement View Details
	}

	private fun updateProfileDetails(user: User) {
		val (_, fullName, email, phone, _) = user
		tv_full_name.text = fullName
		tv_email.text = email
		tv_phone.text = phone
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (AuthenticationUtils.currentUser == null) {
			context?.let {
				BinaryDialog(it).apply {
					title = "Unsigned user"
					description = "Please Sign in or Sign Up to Continue"
					leftButtonName = "Sign In"
					rightButtonName = "Sign Up"
					leftButtonCallback = { findNavController().navigate(R.id.signInFragment) }
					rightButtonCallback = { findNavController().navigate(R.id.signUpFragment) }
				}.build()
			}
			return inflater.inflate(R.layout.unsigned_user_layout, container, false)
		}
		return inflater.inflate(R.layout.fragment_profile, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		rv_user_events.onFlingListener = (object :
			OnFlingListener() {
			private fun updateLayout(height: Int) {

				val layoutParams = root_profile_data.layoutParams
				Utils.convertDPtoPX(context, height)?.let {
					layoutParams.height = it
				}
				root_profile_data.layoutParams = layoutParams
				TransitionManager.beginDelayedTransition(root_profile, ChangeBounds())
			}

			override fun onFling(velocityX: Int, velocityY: Int): Boolean {
				// +ve Y = Swipe Up, -ve Y = Swipe Down
				if (velocityY > 1000) {
					Log.d(TAG, "Fling values = $velocityX, $velocityY")
					updateLayout(150)
					return true
				} else if (velocityY < -2000) {
					Log.d(TAG, "Fling values = $velocityX, $velocityY")
					updateLayout(250)
					return true
				}
				return false
			}
		})
	}


}
