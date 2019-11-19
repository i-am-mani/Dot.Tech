package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.text.Html
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.omega.dottech2k20.Adapters.EventImageItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.BinaryDialog
import com.omega.dottech2k20.Utils.HorizontalEqualSpaceItemDecoration
import com.omega.dottech2k20.Utils.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_details.*

class FragmentEventDetails : Fragment() {

	private val TAG: String = javaClass.simpleName
	lateinit var mEvent: Event
	private val mAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
	val SWIPE_THRESHOLD = 2000
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
		hideButtons()
		initRV()
		initEventDetails()
		attachFlingListener()
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
					BinaryDialog(c, R.layout.dialog_event_confirmation).apply {
						title = "Join This Event ?"
						rightButtonCallback = {
							Log.d(TAG, "btn_join triggered")
							mViewModel.joinEvent(mEvent)
						}
						leftButtonCallback = { }
					}.build()
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

	private fun attachFlingListener() {

		val gestureDetector = GestureDetector(context, FlingGestureListener())
		sv_event_details.setOnTouchListener { v, event ->
			gestureDetector.onTouchEvent(event)
		}
	}

	inner class FlingGestureListener : GestureDetector.SimpleOnGestureListener() {

		/**
		 * On Bottom-Top Fling use alternative layout - excluding images
		 * On Top-Bottom Fling use normal layout with images
		 */
		override fun onFling(
			e1: MotionEvent?,
			e2: MotionEvent?,
			velocityX: Float,
			velocityY: Float
		): Boolean {
			// Swipe from bottom to top is +ve and Swipe from top to bottom in -ve
			if (velocityY > SWIPE_THRESHOLD) {
				Log.d(TAG, "Swipe Detected!")
				val layout = R.layout.fragment_event_details
				val animDuration = 500L
				applyConstraints(layout, animDuration)

			} else if (velocityY < -SWIPE_THRESHOLD) {
				val layout = R.layout.fragment_event_details_maximized
				val animDuration = 500L
				root_event_images.visibility = View.GONE
				applyConstraints(layout, animDuration)
			}

			Log.d(TAG, "vX = $velocityX, vY = $velocityY")
			return super.onFling(e1, e2, velocityX, velocityY)
		}

		private fun applyConstraints(layout: Int, animDuration: Long) {
			val set = ConstraintSet()
			set.clone(context, layout)
			val changeBounds = ChangeBounds()
			changeBounds.apply {
				duration = animDuration
				interpolator = AccelerateDecelerateInterpolator()
			}
			set.applyTo(root_event_details)
			TransitionManager.beginDelayedTransition(root_event_details, changeBounds)
		}
	}

	private fun initEventDetails() {
		tv_event_title.text = mEvent.title
		tv_event_details.text = Html.fromHtml(mEvent.longDescription, Html.FROM_HTML_MODE_LEGACY)
	}

	private fun initRV() {
		generateItems()

		rv_event_images.adapter = mAdapter
		rv_event_images.layoutManager =
			LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
		context?.let {
			LinearSnapHelper().attachToRecyclerView(rv_event_images)
			rv_event_images.addItemDecoration(
				HorizontalEqualSpaceItemDecoration(Utils.convertDPtoPX(it, 10))
			)
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
