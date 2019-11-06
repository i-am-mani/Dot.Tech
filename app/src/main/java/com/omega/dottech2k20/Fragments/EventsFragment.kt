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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.omega.dottech2k20.Adapters.EventItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.BinaryDialog
import com.omega.dottech2k20.Utils.Utils.getEventSchedule
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
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
	private val mFirestore = FirebaseFirestore.getInstance()
	val TAG = javaClass.simpleName
	var mAdapter: GroupAdapter<GroupieViewHolder>? = null
	lateinit var mLayoutManager: CardSliderLayoutManager
	lateinit var mMainActivity: MainActivity
	var isInitTextSet: Boolean = false
	lateinit var mViewModel: UserEventViewModel
	var mEventList: List<Event>? = null
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
		mViewModel.getEvents().observe(this, Observer { events ->
			if (events != null) {
				mEventList = events
				if (mAdapter == null) {
					initRV()
				} else {
					mAdapter?.let {
						getEventItems(events)?.let { mAdapter?.update(it) }
					}
				}

				initCallbacks()
			}
		})

//		btn_join.background.colorFilter =

		btn_join.isClickable = false

		if (AuthenticationUtils.currentUser != null) {

			mViewModel.getUserEvent()?.observe(this, Observer {
				if (it != null) {
					mUserEventList = it
					btn_join.isClickable = true
//					btn_join.setBackgroundResource(R.color.MaterialGreen)
					btn_join.invalidate()
					val activeCardPosition = mLayoutManager.activeCardPosition
					val event: Event = getEvenAtPos(activeCardPosition)
					updateButtons(event)
				}
			})
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
		ts_title.setFactory(TextViewFactory(R.style.TextAppearance_MaterialComponents_Headline5))
		ts_date.setFactory(TextViewFactory(R.style.TextAppearance_MaterialComponents_Body1))
		ts_description.setFactory(TextViewFactory(R.style.TextAppearance_AppCompat_Large))
		ts_participants_count.setFactory(TextViewFactory((R.style.TextAppearance_MaterialComponents_Body1)))

	}

	private fun initCallbacks() {
		setJoinEventCallback()
		setLeaveEventCallback()
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
							mViewModel.joinEvent(getEvenAtPos(activeCard))

							btn_join.animate().alpha(0f).withEndAction {
								btn_join.visibility = View.GONE
								btn_join.alpha = 1f
								btn_unjoin.visibility = View.VISIBLE
							}
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
						val event: Event = getEvenAtPos(activeCard)
						mViewModel.unjoinEvents(event)

						btn_unjoin.animate().alpha(0f).withEndAction {
							btn_unjoin.visibility = View.GONE
							btn_join.visibility = View.VISIBLE
							btn_unjoin.alpha = 1f
							btn_join.alpha = 1f
						}
					}
					leftButtonCallback = { }
				}.build()
			}


		}
	}

	private fun initRV() {
		mAdapter = GroupAdapter()
		rv_event_thumb_nails.setHasFixedSize(true)
		setRecyclerViewLayoutManager()
		setRecyclerViewAdapter()
		setRecyclerViewListener()
		changeEventContent(0)

		CardSnapHelper().attachToRecyclerView(rv_event_thumb_nails)
	}

	private fun setRecyclerViewLayoutManager() {
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

	fun getEvenAtPos(position: Int): Event {
		val value: EventItem = mAdapter?.getItem(position) as EventItem
		return value.event
	}

	private fun setRecyclerViewAdapter() {
		mEventList?.let {
			getEventItems(it)?.let { eventItems ->
				mAdapter?.addAll(eventItems)
			}
		}
		rv_event_thumb_nails.adapter = mAdapter
	}

	private fun getEventItems(events: List<Event>): List<EventItem>? {
		val list = arrayListOf<EventItem>()
		if (mEventList != null && mEventList?.count() ?: 0 > 0) {
			for (event in events) {
				list.add(EventItem(event))
			}
			return list
		}
		return null
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
		val event: Event = getEvenAtPos(position)
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
		setDescription(event.shortDescription, TextAnimationType.FADE_IN)
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


	private fun setDescription(description: String?, animationType: TextAnimationType) {
		setTextSwitcherAnimation(ts_description, animationType)
		ts_description.setText(Html.fromHtml(description?.trim(), Html.FROM_HTML_MODE_COMPACT))
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


	inner class TextViewFactory(val resStyle: Int?) : ViewFactory {
		override fun makeView(): View {
			val textView = TextView(mMainActivity)
			if (resStyle != null) {
				textView.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
				textView.setTextAppearance(resStyle)
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
