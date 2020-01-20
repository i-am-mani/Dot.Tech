package com.omega.dottech2k20.Fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omega.dottech2k20.Adapters.EventParticipantItem
import com.omega.dottech2k20.Adapters.HeaderCountItem
import com.omega.dottech2k20.R
import com.omega.dottech2k20.models.Event
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.fragment_event_participants.*

/**
 * Responsible for displaying participants of passed Event in arguments.
 */
class EventParticipantsFragment : Fragment() {

	private val TAG = "EventParticipantsFragment"
	private lateinit var mAdapter: GroupAdapter<GroupieViewHolder>
	private lateinit var mParticipantsSections: Section


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mAdapter = GroupAdapter()
		mParticipantsSections = Section()
		mAdapter.add(mParticipantsSections)
		return inflater.inflate(R.layout.fragment_event_participants, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRV()
		setUpScrollListener()
	}

	private fun initRV() {
		val event = extractEvent()

		val participantCount = event?.participantCount
		if (participantCount != null && participantCount >= 0) {
			val listOfNames = getParticipantsItems(event)

			mParticipantsSections.setHeader(HeaderCountItem("Total Participants", participantCount))
			mParticipantsSections.addAll(listOfNames)
			rv_participants.adapter = mAdapter
			rv_participants.layoutManager =
				LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
		}
	}

	private fun setUpScrollListener() {

		rv_participants.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val view = recyclerView.getChildAt(0)
				context?.let { ctx ->
					if (view != null && recyclerView.getChildAdapterPosition(view) == 0) {
						val card = view.findViewById<CardView>(R.id.card_total_count)
						// Basically we are moving against the scroll, applying same magnitude of scroll on opposite direction
						card.translationY = -(view.top) / 2f
					}
				}
			}
		})
	}

	private fun getParticipantsItems(event: Event): MutableList<EventParticipantItem> {
		val listOfNames = mutableListOf<EventParticipantItem>()

		for (participant in event.visibleParticipants) {
			participant.let {
				if (it != null) {
					val name = it.name
					if (name != null) {
						listOfNames.add(EventParticipantItem(name))
					}
				}
			}

		}

		event.participantCount.let { participantCount ->
			val anonCount = participantCount - listOfNames.count()
			if (anonCount > 0) {
				listOfNames.add(EventParticipantItem("+ $anonCount Anonymous Participants"))
			}
		}
		return listOfNames
	}

	private fun extractEvent(): Event? {
		val event = arguments?.getParcelable<Event>(BUNDLE_KEY)
		if (event != null) {
			return event
		} else {
			Log.e(TAG, "Null Event Passed", NullPointerException())
			findNavController().navigate(R.id.eventsFragment)
		}
		return null
	}


	companion object {
		val BUNDLE_KEY = "participants"
	}


}
