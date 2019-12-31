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
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_event_participants.*
import kotlinx.android.synthetic.main.item_total_count.*

/**
 * A simple [Fragment] subclass.
 */
class EventParticipantsFragment : Fragment() {

	val TAG = "EventParticipantsFragment"


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_event_participants, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val adapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
		val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
		initRV(adapter, layoutManager)
		setUpScrollListener()
	}

	private fun initRV(
		adapter: GroupAdapter<GroupieViewHolder>,
		layoutManager: LinearLayoutManager
	) {
		val event = extractEvent()

		val participantCount = event?.participantCount
		if (participantCount != null && participantCount > 0) {
			val listOfNames = getParticipantsItems(event)
			adapter.add(TotalItem(participantCount))
			adapter.addAll(listOfNames)
			rv_participants.adapter = adapter
			rv_participants.layoutManager = layoutManager
		}
	}

	private fun setUpScrollListener() {
		rv_participants.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val view = recyclerView.getChildAt(0)
				if (view != null && recyclerView.getChildAdapterPosition(view) === 0) {
					val card = view.findViewById<CardView>(R.id.card_profile_events_summary)
					Log.d(TAG, "view.top = ${view.top}")
					// Basically we are moving against the scroll, applying same magnitude of scroll on opposite direction
					card.translationX = -(view.top) / 2f
				}
			}
		})
	}

	private fun getParticipantsItems(event: Event): MutableList<EventParticipantItem> {
		val names = event.visibleParticipants.values
		val listOfNames = mutableListOf<EventParticipantItem>()

		for (name in names) {
			listOfNames.add(EventParticipantItem(name))
		}

		event.participantCount?.let { participantCount ->
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

	inner class TotalItem(val count: Int) : Item() {
		override fun bind(
			viewHolder: com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder,
			position: Int
		) {
			viewHolder.apply {
				tv_total_count.text = count.toString()
			}
		}

		override fun getLayout(): Int {
			return R.layout.item_total_count
		}

	}

	companion object {
		val BUNDLE_KEY = "participants"
	}


}
