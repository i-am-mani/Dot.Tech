package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.omega.dottech2k20.Adapters.UserEventItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.Models.User
import com.omega.dottech2k20.Models.UserEventViewModel


import com.omega.dottech2k20.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
	val TAG = javaClass.simpleName
	lateinit var mViewModel: UserEventViewModel
	var mAdapter = GroupAdapter<GroupieViewHolder>()

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mViewModel = ViewModelProviders.of(context as MainActivity).get(UserEventViewModel::class.java)
		val profile = mViewModel.getUserProfile()
		val userProfile = profile?.observe(this, Observer {
			if(it!=null){
				updateProfileDetails(it)
			}
		})
		val userEvents = mViewModel.getUserEvent()?.observe(this,Observer {
			Log.d("ViewModel", "Event list = ${it?.count()?:0}")
			if(it != null){
				updateUserEvents(it)
			}
		})
	}

	private fun updateUserEvents(events: List<Event>) {
		rv_user_events.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
		val eventItems = getEventItems(events)
		mAdapter.addAll(eventItems)
		rv_user_events.adapter = mAdapter
	}

	private fun getEventItems(events:List<Event>): List<UserEventItem>{
		val list = arrayListOf<UserEventItem>()
		for (event in events){
			list.add(UserEventItem(event))
		}
		return list
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
