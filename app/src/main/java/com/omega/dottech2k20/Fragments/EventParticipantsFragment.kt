package com.omega.dottech2k20.Fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omega.dottech2k20.Adapters.EventParticipantItem
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_participants.*

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

		val event = extractEvent()
		val listOfNames = mutableListOf<EventParticipantItem>()

		if (event != null) {
			val names = event.visibleParticipants.values

			for (name in names) {
				listOfNames.add(EventParticipantItem(name))
			}

			event.participantCount?.let { participantCount ->
				val anonCount = participantCount - listOfNames.count()
				if (anonCount > 0) {
					listOfNames.add(EventParticipantItem("+ $anonCount Anonymous Participants"))
				}
			}

			adapter.addAll(listOfNames)

			rv_participants.adapter = adapter
			rv_participants.layoutManager = layoutManager
		}
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
