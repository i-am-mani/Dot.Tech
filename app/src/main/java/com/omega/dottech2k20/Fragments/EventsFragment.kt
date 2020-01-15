package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher.ViewFactory
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.firebase.Timestamp
import com.omega.dottech2k20.Adapters.EventImageAdapter
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.EventCallbacks
import com.omega.dottech2k20.Utils.FirestoreFieldNames
import com.omega.dottech2k20.Utils.Utils
import com.omega.dottech2k20.Utils.Utils.getEventSchedule
import com.omega.dottech2k20.models.Event
import com.omega.dottech2k20.models.UserEventViewModel
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import kotlinx.android.synthetic.main.fragment_events.*


class EventsFragment : Fragment() {

	enum class TextAnimationType {
		RIGHT_TO_LEFT,
		LEFT_TO_RIGHT,
		FADE_IN,
		TOP_TO_BOTTOM,
		BOTTOM_TO_TOP
	}

	private var mCurrentPosition: Int? = null
	private val TAG = javaClass.simpleName
	private var mAdapter: EventImageAdapter? = null
	lateinit var mLayoutManager: CardSliderLayoutManager
	lateinit var mMainActivity: MainActivity
	lateinit var mViewModel: UserEventViewModel
	var mUserEventList: List<Event>? = null

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mMainActivity = context as MainActivity
	}

	override fun onDestroyView() {
		super.onDestroyView()
		mAdapter = null
	}

	override fun onPause() {
		super.onPause()
		val index = mLayoutManager.activeCardPosition
		mViewModel.setEventIndex(index) // To restore previous state
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mMainActivity).get(UserEventViewModel::class.java)
		mViewModel.getEvents().observe(this, getEventsObserver())

		val currentUser = AuthenticationUtils.currentUser
		Log.d(TAG, "Current User is email verified = ${currentUser?.isEmailVerified}")
		btn_join?.isEnabled = false // disable join button until UserEventData is fetched.
		mViewModel.getUserEvent()?.observe(this, getUserEventObserver())

	}

	private fun getUserEventObserver(): Observer<List<Event>> {
		return Observer {
			if (it != null) {
				mUserEventList = it
				btn_join.isEnabled = true
				// enable join button since, we know Event's user is part of.
				val activeCardPosition = mLayoutManager.activeCardPosition
				if (activeCardPosition != RecyclerView.NO_POSITION) {
					Log.d(TAG, "position = $activeCardPosition")
					val event: Event? = getEventAtPos(activeCardPosition)
					if (event != null) {
						updateButtons(event)
					}
				}
			}
		}
	}

	private fun getEventsObserver(): Observer<List<Event>> {
		return Observer { events ->
			if (events != null) {
				if (mAdapter == null) {
					initRV(events)
				} else {
					updateAdapterDataset(events)
				}
				initCallbacks()
				if (events.count() > 0) {
					group_loaders.visibility = GONE
					group_events_elements.visibility = VISIBLE
				}
			}
		}
	}

	private fun updateAdapterDataset(events: List<Event>) {
		mAdapter?.let {
			val pos = mLayoutManager.activeCardPosition

			// Update the participant Count in case it's updated in new list.
			val item = it.getItem(pos)
			if (item != null && item.participantCount != events[pos].participantCount) {
				setParticipantCount(
					events[pos].participantCount,
					TextAnimationType.LEFT_TO_RIGHT
				)
			}

			// update the data set.
			// Note:- Changing DataSet wouldn't trigger any change on current visible item.
			it.setDataSet(events)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_events, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		ts_title.setFactory(TextViewFactory(R.style.TitleTextEczar))
		ts_date.setFactory(TextViewFactory(R.style.DateTimeAppearance))
		ts_participants_count.setFactory(TextViewFactory((R.style.TextAppearance_MaterialComponents_Body1)))
	}

	private fun initCallbacks() {
		setJoinEventCallback()
		setLeaveEventCallback()
		setViewDetailsListener()
		setViewParticipantsCallback()
	}

	private fun setViewParticipantsCallback() {
		imbtn_view_participants.setOnClickListener {
			val activeCardPosition = mLayoutManager.activeCardPosition
			if (activeCardPosition != RecyclerView.NO_POSITION) {
				val event: Event? = getEventAtPos(activeCardPosition)
				if (event != null) {
					if (event.type == FirestoreFieldNames.EVENT_TYPE_INDIVIDUAL) {
						findNavController().navigate(
							R.id.eventParticipantsFragment,
							bundleOf(EventParticipantsFragment.BUNDLE_KEY to event)
						)
					} else if (event.type == FirestoreFieldNames.EVENT_TYPE_TEAM) {
						findNavController().navigate(
							R.id.eventTeamsFragment,
							bundleOf(
								EventTeamsFragment.EVENT_KEY to event,
								EventTeamsFragment.READ_ONLY to true
							)
						)
					}
				}

			}
		}
	}

	private fun setViewDetailsListener() {
		btn_details.setOnClickListener {
			val activeCard: Int = mLayoutManager.activeCardPosition
			val event: Event? = getEventAtPos(activeCard)
			if (event != null) {
				findNavController().navigate(
					R.id.event_details,
					bundleOf(EventDetailsFragment.EVENT_KEY to event)
				)
			}
		}
	}

	private fun setJoinEventCallback() {
		context?.let { ctx ->
			btn_join.setOnClickListener {
				Utils.contextClickHapticFeedback(it)
				val activeCard: Int = mLayoutManager.activeCardPosition
				val event = getEventAtPos(activeCard)

				val authenticationDialogStatus =
					EventCallbacks.authenticationDialog(ctx, findNavController())
				if (authenticationDialogStatus && event != null) {
					when {
						event.type == FirestoreFieldNames.EVENT_TYPE_INDIVIDUAL -> {
							EventCallbacks.joinEvent(ctx, event, mViewModel)
						}
						event.type == FirestoreFieldNames.EVENT_TYPE_TEAM -> {
							findNavController().navigate(
								R.id.eventTeamsFragment,
								bundleOf(
									EventTeamsFragment.EVENT_KEY to event,
									EventTeamsFragment.READ_ONLY to false
								)
							)
						}

					}
				} else {
					Utils.virtualClickHapticFeedback(it)
				}

			}
		}
	}

	private fun setLeaveEventCallback() {
		btn_leave.setOnClickListener {
			Utils.contextClickHapticFeedback(it)
			val activeCard: Int = mLayoutManager.activeCardPosition
			val event: Event? = getEventAtPos(activeCard)
			if (event != null) {
				if (event.type == FirestoreFieldNames.EVENT_TYPE_INDIVIDUAL) {
					context?.let {
						EventCallbacks.leave(it, event, mViewModel)
					}
				} else if (event.type == FirestoreFieldNames.EVENT_TYPE_TEAM) {
					findNavController().navigate(
						R.id.eventTeamsFragment,
						bundleOf(
							EventTeamsFragment.EVENT_KEY to event,
							EventTeamsFragment.READ_ONLY to false
						)
					)
				}
			}
		}

	}

	private fun initRV(events: List<Event>) {
		setRecyclerViewLayoutManager()
		setRecyclerViewAdapter(events)
		setRecyclerViewListener()

		val index = mViewModel.getEventIndex() ?: 0
		changeEventContent(index)
		mLayoutManager.scrollToPosition(index)

		CardSnapHelper().attachToRecyclerView(rv_event_thumb_nails)
	}

	private fun setRecyclerViewAdapter(events: List<Event>) {
		context?.let { ctx ->
			mAdapter = EventImageAdapter(ctx)
			mAdapter?.let {
				it.setDataSet(events)
				rv_event_thumb_nails.adapter = mAdapter
			}
		}
	}

	private fun setRecyclerViewLayoutManager() {
		rv_event_thumb_nails.setHasFixedSize(true)
		mLayoutManager = CardSliderLayoutManager(
			Utils.convertDPtoPX(context!!, 75),
			resources.getDimensionPixelSize(R.dimen.event_card_width),
			Utils.convertDPtoPX(context!!, 15).toFloat()
		)
		rv_event_thumb_nails.layoutManager = mLayoutManager
	}

	private fun setRecyclerViewListener() {
		rv_event_thumb_nails.addOnScrollListener(object :
			OnScrollListener() {
			override fun onScrollStateChanged(
				recyclerView: RecyclerView,
				newState: Int
			) {
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//					if(btn_join != null && btn_leave != null){
//						btn_join.isEnabled = true
//						btn_leave.isEnabled = true
//					}
					onCardChanged()
				} else {
					if (btn_join != null && btn_leave != null) {
						btn_join.isEnabled = false
						btn_leave.isEnabled = false
					}
				}
			}
		})
	}

	private fun getEventAtPos(position: Int): Event? {
		return mAdapter?.getItem(position)
	}


	private fun updateButtons(event: Event) {

		val matchingEvent = mUserEventList?.find {
			//			Log.d(TAG, "it.id = ${it.id}, even.id = ${event.id}")
			it.id == event.id
		}
		// null event would imply that event doesn't exist in user's events field
		if (matchingEvent == null) {
			btn_join.visibility = VISIBLE
			btn_leave.visibility = GONE
		} else {
			btn_join.visibility = GONE
			btn_leave.visibility = VISIBLE
		}
		// Disable the button if registration is closed, done after changing visibilities since
		// buttons are disabled when they come out of scrolled state (Look at onScrollListener of rv)
		btn_join.isEnabled = event.registrationOpen
		btn_leave.isEnabled = event.registrationOpen

	}


	private fun onCardChanged() {
		val activeCardPosition = mLayoutManager.activeCardPosition
		if (activeCardPosition == RecyclerView.NO_POSITION || activeCardPosition == mCurrentPosition) {
			return
		} else {
			changeEventContent(activeCardPosition)
		}
	}

	private fun changeEventContent(position: Int) {
		val event: Event? = getEventAtPos(position)
		if (event != null) {
			var animTypeHorizontal = TextAnimationType.RIGHT_TO_LEFT
			var animTypeVertical = TextAnimationType.TOP_TO_BOTTOM

			if (position < mCurrentPosition ?: 0) {
				animTypeHorizontal = TextAnimationType.LEFT_TO_RIGHT
				animTypeVertical = TextAnimationType.BOTTOM_TO_TOP
			}
			updateButtons(event)
			setTitle(event.title, animTypeHorizontal)
			setDateTime(event.startTime, event.endTime, animTypeVertical)
			setDescription(event.shortDescription)
			setParticipantCount(event.participantCount, animTypeVertical)
			mCurrentPosition = position
		}

		if (position == mAdapter?.itemCount?.minus(1)) {
			imbtn_events_next.animate().rotation(180f)
		} else if (position == 0) {
			imbtn_events_next.animate().rotation(0f)
		} else {
			imbtn_events_next.animate().scaleX(0f).scaleY(0f).setDuration(300).withEndAction {
				imbtn_events_next.animate().scaleX(1f).scaleY(1f).duration = 500
			}
		}
	}

	private fun setParticipantCount(
		participantCount: Int?,
		animTypeVertical: TextAnimationType
	) {
		ts_participants_count.setText(participantCount.toString())
		setTextSwitcherAnimation(ts_participants_count, animTypeVertical)
	}

	private fun setDateTime(
		startTime: Timestamp?,
		endTime: Timestamp?,
		animTypeVertical: TextAnimationType
	) {
		if (startTime != null && endTime != null) {
			val time = getEventSchedule(startTime, endTime)
			setTextSwitcherAnimation(ts_date, animTypeVertical)
			ts_date.setText(time)
		}
	}

	private fun setDescription(description: String?) {
		tv_description.text = Html.fromHtml(description?.trim(), Html.FROM_HTML_MODE_COMPACT)
	}

	private fun setTitle(title: String?, animationType: TextAnimationType) {

		setTextSwitcherAnimation(ts_title, animationType)
		ts_title.setText(title)
	}

	private fun setTextSwitcherAnimation(ts: TextSwitcher, animationType: TextAnimationType) {

		when (animationType) {
			TextAnimationType.LEFT_TO_RIGHT -> {
				ts.setInAnimation(context, R.anim.slide_in_left)
				ts.setOutAnimation(context, R.anim.slide_out_right)
			}
			TextAnimationType.RIGHT_TO_LEFT -> {
				ts.setInAnimation(context, R.anim.slide_in_right)
				ts.setOutAnimation(context, R.anim.slide_out_left)
			}
			TextAnimationType.FADE_IN -> {
				ts.setInAnimation(context, R.anim.fade_in)
				ts.setInAnimation(context, R.anim.fade_out)
			}
			TextAnimationType.TOP_TO_BOTTOM -> {
				ts.setInAnimation(context, R.anim.slide_in_top)
				ts.setOutAnimation(context, R.anim.slide_out_bottom)
			}
			TextAnimationType.BOTTOM_TO_TOP -> {
				ts.setInAnimation(context, R.anim.slide_in_bottom)
				ts.setOutAnimation(context, R.anim.slide_out_top)
			}
		}
	}

	inner class TextViewFactory(private val resStyle: Int?) : ViewFactory {
		override fun makeView(): View {
			val textView = TextView(mMainActivity)
			if (resStyle != null) {
				textView.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
				textView.setTextAppearance(resStyle)
				textView.typeface = ResourcesCompat.getFont(context!!, R.font.eczar_medium)
				textView.gravity = Gravity.CENTER_VERTICAL
			}

			return textView
		}

	}
}
