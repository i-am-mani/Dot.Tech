package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher.ViewFactory
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.omega.dottech2k20.Adapters.EventsHolder
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import kotlinx.android.synthetic.main.fragment_events.*
import java.text.SimpleDateFormat

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
    lateinit var mAdapter: FirestoreRecyclerAdapter<Event?, EventsHolder?>
    lateinit var mLayoutManager: CardSliderLayoutManager
    lateinit var mMainActivity: MainActivity
    var isInitTextSet: Boolean = false
    lateinit var mViewModel: UserEventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mMainActivity = context as MainActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(UserEventViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_events, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ts_title.setFactory(TextViewFactory(R.style.TextAppearance_MaterialComponents_Headline5))
        ts_date.setFactory(TextViewFactory(R.style.TextAppearance_MaterialComponents_Body1))
        ts_description.setFactory(TextViewFactory(R.style.TextAppearance_AppCompat_Large))
        initRV()
        initCallbacks()
    }

    private fun initCallbacks() {
        btn_join.setOnClickListener{
            val activeCard: Int = mLayoutManager.activeCardPosition
            val event: Event = mAdapter.getItem(activeCard)
            mViewModel.joinEvent(event)
        }

        btn_unjoin.setOnClickListener{
            val activeCard: Int = mLayoutManager.activeCardPosition
            val event: Event = mAdapter.getItem(activeCard)
            mViewModel.unjoinEvents(event)
        }
    }

    private fun initRV() {
        rv_event_thumb_nails.setHasFixedSize(true)

        val snapshotParser = SnapshotParser { it: DocumentSnapshot ->
            val event = it.toObject(Event::class.java)!!
            event.id = it.id
            return@SnapshotParser event
        }

        val reference: Query = mFirestore.collection("Events")
        val options: FirestoreRecyclerOptions<Event?> = getOptions(reference, snapshotParser)

        setRecyclerViewAdapter(options)
        setRecyclerViewLayoutManager()
        setRecyclerViewListener()

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

    private fun getOptions(
        reference: Query,
        snapshotParser: SnapshotParser<Event>
    ): FirestoreRecyclerOptions<Event?> {

        return FirestoreRecyclerOptions.Builder<Event>()
            .setQuery(reference, snapshotParser)
            .build()
    }

    private fun setRecyclerViewAdapter(options: FirestoreRecyclerOptions<Event?>) {
        mAdapter = object :
            FirestoreRecyclerAdapter<Event?, EventsHolder?>(options) {
            override fun onBindViewHolder(holder: EventsHolder, position: Int, model: Event) {
                holder.onBind(model.thumbNail!!)
                Log.d(TAG, model.title)
                // One time Initialization
                if (!isInitTextSet && ts_title != null) {
                    setTitle(model.title,
                        TextAnimationType.FADE_IN
                    )
                    isInitTextSet = true
                }
            }

            override fun onCreateViewHolder(group: ViewGroup, i: Int): EventsHolder {
                val view: View = LayoutInflater.from(group.context)
                    .inflate(R.layout.adapter_events, group, false)
                return EventsHolder(view)
            }
        }

        rv_event_thumb_nails.adapter = mAdapter
        mAdapter.startListening()
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
        val event: Event = mAdapter.getItem(position)
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

        setTitle(event.title, animTypeHorizontal)
        setDateTime(event.startTime, event.endTime, animTypeVertical)
        setDescription(event.shortDescription,
            TextAnimationType.FADE_IN
        )

        mCurrentPosition = position

    }

    private fun setDateTime(
        startTime: Timestamp?,
        endTime: Timestamp?,
        animTypeVertical: TextAnimationType
    ) {

        val dateFormatter = SimpleDateFormat("MMM, EEE")
        val timeFormatter = SimpleDateFormat("hh:mm")


        if (startTime != null && endTime != null) {
            val date = startTime.toDate()
            val eventDate = dateFormatter.format(date)

            val eventStartTime = timeFormatter.format(date)
            val eventEndTime = timeFormatter.format(endTime.toDate())
            val time = "$eventDate    $eventStartTime - $eventEndTime"

            setTextSwitcherAnimation(ts_date, animTypeVertical)

            ts_date.setText(time)

            Log.d(TAG, "date = $eventDate , startTime = $eventStartTime, endTime = $eventEndTime")
        }

    }

    private fun setDescription(description: String?, animationType: TextAnimationType) {
        setTextSwitcherAnimation(ts_description, animationType)
        ts_description.setText(description)
    }

    private fun setTitle(title: String?, animationType: TextAnimationType) {
        Log.d(TAG, title)
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
                textView.setTextAppearance(resStyle)
                textView.gravity = Gravity.CENTER_VERTICAL
                textView.minHeight = ViewGroup.LayoutParams.MATCH_PARENT
            }

            return textView
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("LAYOUT",mLayoutManager.onSaveInstanceState())
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.stopListening()
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            EventsFragment()
    }
}
