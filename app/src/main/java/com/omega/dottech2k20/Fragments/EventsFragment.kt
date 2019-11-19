package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher.ViewFactory
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.firebase.Timestamp
import com.omega.dottech2k20.Adapters.EventImageAdapter
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.BinaryDialog
import com.omega.dottech2k20.Utils.Utils.getEventSchedule
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
	val TAG = javaClass.simpleName
	var mAdapter: EventImageAdapter? = null
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

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		Log.d(TAG, "onActivityCreated prompted, mAdapter = $mAdapter")
		mViewModel = ViewModelProviders.of(mMainActivity).get(UserEventViewModel::class.java)
		mViewModel.getEvents().observe(this, getEventsObserver())

		if (AuthenticationUtils.currentUser != null) {
			mViewModel.getUserEvent()?.observe(this, getUserEventObserver())
		}
	}

	private fun getUserEventObserver(): Observer<List<Event>> {
		return Observer {
			if (it != null) {
				mUserEventList = it
				btn_join.isClickable = true
				val activeCardPosition = mLayoutManager.activeCardPosition
				if (activeCardPosition != RecyclerView.NO_POSITION) {
					Log.d(TAG, "position = $activeCardPosition")
					val event: Event = getEventAtPos(activeCardPosition)
					updateButtons(event)
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
			}
		}
	}

	private fun updateAdapterDataset(events: List<Event>) {
		mAdapter?.let {
			val pos = mLayoutManager.activeCardPosition

			// Update the participant Count in case it's updated in new list.
			if (it.getItem(pos)?.participantCount != events[pos].participantCount) {
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

	private fun updateButtons(event: Event) {

		val matchingEvent = mUserEventList?.find {
			Log.d(TAG, "it.id = ${it.id}, even.id = ${event.id}")
			it.id == event.id
		}
		if (matchingEvent == null) {
			Log.d(TAG, "Changing Visibility")
			btn_join.animate().alpha(1f).withEndAction {
				btn_join.visibility = View.VISIBLE
				btn_unjoin.visibility = View.GONE
				btn_unjoin.alpha = 1f
				btn_join.alpha = 1f
			}
		} else {
			btn_unjoin.animate().alpha(1f).withEndAction {
				btn_join.visibility = View.GONE
				btn_unjoin.visibility = View.VISIBLE
				btn_unjoin.alpha = 1f
				btn_join.alpha = 1f
			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_events, container, false)
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		ts_title.setFactory(TextViewFactory(R.style.TitleText))
		ts_date.setFactory(TextViewFactory(R.style.DateTimeAppearance))
		ts_participants_count.setFactory(TextViewFactory((R.style.TextAppearance_MaterialComponents_Body1)))
	}

	private fun initCallbacks() {
		setJoinEventCallback()
		setLeaveEventCallback()
		setViewDetailsListener()
	}

	private fun setViewDetailsListener() {
		btn_details.setOnClickListener {
			val activeCard: Int = mLayoutManager.activeCardPosition
			val event: Event = getEventAtPos(activeCard)
			val bundle = Bundle()
			Log.d(TAG, "Event = $event")
			bundle.putParcelable("event", event)
			findNavController().navigate(R.id.event_details, bundle)
		}
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
							val activeCard: Int = mLayoutManager.activeCardPosition
							mViewModel.joinEvent(getEventAtPos(activeCard))
						}
						leftButtonCallback = { }
					}.build()
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

	private fun setLeaveEventCallback() {
		btn_unjoin.setOnClickListener {

			context?.let { c ->
				BinaryDialog(c, R.layout.dialog_event_confirmation).apply {
					title = "Leave this Event ?"
					rightButtonCallback = {
						val activeCard: Int = mLayoutManager.activeCardPosition
						val event: Event = getEventAtPos(activeCard)
						mViewModel.unjoinEvents(event)
					}
					leftButtonCallback = { }
				}.build()
			}


		}
	}

	private fun initRV(events: List<Event>) {
		setRecyclerViewLayoutManager()
		setRecyclerViewAdapter(events)
		setRecyclerViewListener()
		changeEventContent(0)

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
		mLayoutManager = CardSliderLayoutManager(context!!)
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
					onCardChanged()
				}
			}
		})
	}

	private fun getEventAtPos(position: Int): Event {

		return mAdapter!!.getItem(position)!!
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
		val event: Event = getEventAtPos(position)
		var animTypeHorizontal =
			TextAnimationType.RIGHT_TO_LEFT
		var animTypeVertical =
			TextAnimationType.TOP_TO_BOTTOM

		if (position < mCurrentPosition ?: 0) {
			animTypeHorizontal =
				TextAnimationType.LEFT_TO_RIGHT
			animTypeVertical =
				TextAnimationType.BOTTOM_TO_TOP
		}
		updateButtons(event)
		setTitle(event.title, animTypeHorizontal)
		setDateTime(event.startTime, event.endTime, animTypeVertical)
		setDescription(event.shortDescription)
		setParticipantCount(event.participantCount, animTypeVertical)
		mCurrentPosition = position
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
		// animation doesn't seem to be working
//		ts_description.animate().alpha(0f).apply {
//			duration = 500
//		}.start()
//		ts_description.animate().alpha(1f).apply {
//			duration = 600
//			startDelay = 500
//			withStartAction {  }
//		}.start()

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

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putParcelable("LAYOUT", mLayoutManager.onSaveInstanceState())
	}


	companion object {

		@JvmStatic
		fun newInstance() =
			EventsFragment()
	}
}
