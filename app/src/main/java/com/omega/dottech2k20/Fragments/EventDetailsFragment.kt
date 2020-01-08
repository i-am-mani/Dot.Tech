package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omega.dottech2k20.Adapters.EventDetailItem
import com.omega.dottech2k20.Adapters.HorizontalImageViewerItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.EventCallbacks
import com.omega.dottech2k20.Utils.FirestoreFieldNames
import com.omega.dottech2k20.dialogs.BinaryDialog
import com.omega.dottech2k20.models.Event
import com.omega.dottech2k20.models.UserEventViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_details.*

class EventDetailsFragment : Fragment() {

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
		setUpScrollListener()
		addClickListeners()
	}

	private fun setUpScrollListener() {
		rv_event_details.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val view = recyclerView.getChildAt(0)
				if (view != null && recyclerView.getChildAdapterPosition(view) == 0) {
					val card = view.findViewById<CardView>(R.id.card_image_holder)
					// Basically we are moving against the scroll, applying same magnitude of scroll on opposite direction
					card.translationY = -(view.top) / 2f
					btn_join.translationX = -(view.top) / 3f
					btn_leave.translationX = -(view.top) / 3f
				}
			}
		})
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
			context?.let { ctx ->
				val authenticationDialogStatus =
					EventCallbacks.authenticationDialog(ctx, findNavController())
				if (authenticationDialogStatus) {
					when {
						mEvent.type == FirestoreFieldNames.EVENT_TYPE_INDIVIDUAL -> {
							EventCallbacks.joinEvent(ctx, mEvent, mViewModel)
						}
						mEvent.type == FirestoreFieldNames.EVENT_TYPE_TEAM -> {
							findNavController().navigate(
								R.id.eventTeamsFragment,
								bundleOf(
									EventTeamsFragment.EVENT_KEY to mEvent,
									EventTeamsFragment.READ_ONLY to false
								)
							)
						}

					}
				}
			}
		}
	}

	private fun setLeaveEventCallback() {
		btn_leave.setOnClickListener {
			context?.let { ctx ->
				val authenticationDialogStatus =
					EventCallbacks.authenticationDialog(ctx, findNavController())
				if (authenticationDialogStatus) {
					when {
						mEvent.type == FirestoreFieldNames.EVENT_TYPE_INDIVIDUAL -> {
							EventCallbacks.leave(ctx, mEvent, mViewModel)
						}
						mEvent.type == FirestoreFieldNames.EVENT_TYPE_TEAM -> {
							findNavController().navigate(
								R.id.eventTeamsFragment,
								bundleOf(
									EventTeamsFragment.EVENT_KEY to mEvent,
									EventTeamsFragment.READ_ONLY to false
								)
							)
						}

					}
				}
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


	companion object {
		const val EVENT_KEY = "Event"
	}

}
