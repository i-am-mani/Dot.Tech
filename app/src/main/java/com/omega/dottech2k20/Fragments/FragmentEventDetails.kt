package com.omega.dottech2k20.Fragments


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omega.dottech2k20.Adapters.EventDetailItem
import com.omega.dottech2k20.Adapters.EventImageItem
import com.omega.dottech2k20.Adapters.HorizontalImageViewerItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.BinaryDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_details.*

class FragmentEventDetails : Fragment() {

	private val TAG: String = javaClass.simpleName
	lateinit var mEvent: Event
	private lateinit var mAdapter: GroupAdapter<GroupieViewHolder>
	lateinit var mActivity: MainActivity
	lateinit var mViewModel: UserEventViewModel

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(UserEventViewModel::class.java)
		mViewModel.getUserEvent()?.observe(this, Observer {
			it?.let {
				val event = it.find { e -> e.id == mEvent.id }
				updateButton(event)
			}
		})
	}

	private fun updateButton(event: Event?) {
		if (event == null) {
			btn_join.show()
			btn_leave.hide()
		} else {
			btn_leave.show()
			btn_join.hide()
		}
	}


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Log.d(TAG, "Bundle values - ${arguments?.toString()}")
		extractEvent()
		return inflater.inflate(R.layout.fragment_event_details, container, false)
	}

	/**
	 * Will store the event in global mEvent variable, in case it's null, Navigate back to Events
	 */
	private fun extractEvent() {
		val event = arguments?.getParcelable<Event>(EVENT_KEY)
		if (event != null) {
			mEvent = event
		} else {
			Log.e(TAG, "Null Event Passed", NullPointerException())
			findNavController().navigate(R.id.eventsFragment)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		btn_join.isEnabled = mEvent.registrationOpen
		btn_leave.isEnabled = mEvent.registrationOpen
		hideButtons()
		initRV()
		addClickListeners()
	}

	private fun hideButtons() {
		btn_leave.hide()
		btn_join.hide()
	}

	private fun addClickListeners() {
		setJoinEventCallback()
		setLeaveEventCallback()
		btn_leave.hide()
	}

	private fun setJoinEventCallback() {
		btn_join.setOnClickListener {
			if (AuthenticationUtils.currentUser == null) {
				requestForLoginDialog()
			} else {
				context?.let { c ->
					Dialog(c).apply {
						setCanceledOnTouchOutside(true)

						setContentView(R.layout.dialog_join_event_confirmation)
						window.setLayout(
							ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT
						)
						window.setBackgroundDrawableResource(android.R.color.transparent)

						val confirmBtn = findViewById<Button>(R.id.btn_right)
						val cancelBtn = findViewById<Button>(R.id.btn_left)
						val chbxAnonUser = findViewById<CheckBox>(R.id.chbx_anon_user)

						confirmBtn.setOnClickListener {
							mViewModel.joinEvent(mEvent, chbxAnonUser.isChecked)
							dismiss()
						}

						cancelBtn.setOnClickListener {
							dismiss()
						}
					}.show()
				}
			}
		}
	}

	private fun navigateToEvents() {
		findNavController().navigate(R.id.eventsFragment)
	}

	private fun setLeaveEventCallback() {
		btn_leave.setOnClickListener {

			context?.let { c ->
				BinaryDialog(c, R.layout.dialog_event_confirmation).apply {
					title = "Leave this Event ?"
					rightButtonCallback = {
						mViewModel.unjoinEvents(mEvent)
					}
					leftButtonCallback = { }
				}.build()
			}


		}
	}

	private fun requestForLoginDialog() {
		context?.let { c ->
			BinaryDialog(c).apply {
				title = "Unsigned user"
				description = "Please Sign in or Sign Up to Continue"
				leftButtonName = "Sign In"
				rightButtonName = "Sign Up"
				rightButtonId = R.id.btn_right
				leftButtonId = R.id.btn_left
				leftButtonCallback = { findNavController().navigate(R.id.signInFragment) }
				rightButtonCallback = { findNavController().navigate(R.id.signUpFragment) }
			}.build()
		}
	}

	private fun initRV() {
		context?.let { ctx ->
			mAdapter = GroupAdapter()

			val imagesItem = HorizontalImageViewerItem(ctx, mEvent.images)
			val eventDetails = EventDetailItem(ctx, mEvent) {
				findNavController().navigate(
					R.id.eventParticipantsFragment,
					bundleOf(EventParticipantsFragment.BUNDLE_KEY to mEvent)
				)
			}

			mAdapter.add(imagesItem)
			mAdapter.add(eventDetails)

			rv_event_details.adapter = mAdapter
			rv_event_details.layoutManager =
				LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
		}

	}


	private fun generateItems() {
		val items = mutableListOf<EventImageItem>()

		mEvent.images?.let { images ->
			for (image in images) {
				Log.d(TAG, "Image - $image added")
				items.add(EventImageItem(image))
			}
		}

		mAdapter.addAll(items)
	}

	companion object {
		const val EVENT_KEY = "Event"
	}

}
