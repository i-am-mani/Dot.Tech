package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omega.dottech2k20.Adapters.TeamItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.dialogs.CreateTeamDialog
import com.omega.dottech2k20.models.Event
import com.omega.dottech2k20.models.Team
import com.omega.dottech2k20.models.Teammate
import com.omega.dottech2k20.models.UserEventViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_event_teams.*


class EventTeamsFragment : Fragment() {

	private val TAG = javaClass.simpleName
	private lateinit var mActivity: MainActivity
	private lateinit var mAdapter: GroupAdapter<GroupieViewHolder>
	private lateinit var mViewModel: UserEventViewModel
	private lateinit var mEvent: Event
	private var isJoinEnabled: Boolean? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mAdapter = GroupAdapter()
		extractArguments()
		return inflater.inflate(R.layout.fragment_event_teams, container, false)
	}

	private fun initRV() {
		rv_teams.adapter = mAdapter
		rv_teams.itemAnimator = null // To prevent blinking of RV
		rv_teams.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRV()
		addFABCallback()
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(UserEventViewModel::class.java)
		mEvent.id?.let { eid ->
			mViewModel.getTeamsOfEvent(eid).observe(this, Observer { teams ->
				if (teams != null) {
					Log.d(TAG, "Teams = $teams")
					val eventTeams = teams[eid]
					if (eventTeams != null) {
						populateAdapter(eventTeams)
					}
				}
			})
		}
	}

	private fun populateAdapter(eventTeams: List<Team>) {
		if (mAdapter.itemCount == 0) {
			mAdapter.addAll(getTeamItems(eventTeams))

		} else {
			mAdapter.update(getTeamItems(eventTeams))
		}
	}

	private fun getTeamItems(eventTeams: List<Team>): List<TeamItem> {
		val listOfItems = mutableListOf<TeamItem>()
		isJoinEnabled?.let { isJoin ->
			context?.let { ctx ->
				for (team in eventTeams) {
					val teamItem = TeamItem(
						ctx,
						team,
						isJoin,
						mEvent.registrationOpen,
						::deleteTeam,
						::removeTeammate,
						::addUserToTeam
					)
					listOfItems.add(teamItem)
				}
			}
		}
		return listOfItems
	}

	private fun removeTeammate(teammate: Teammate) {

	}

	private fun deleteTeam(teamId: String) {
		// TODO implement delete team
	}

	private fun addUserToTeam(teamId: String) {
		// TODO implement user addition to team
	}

	private fun addFABCallback() {
		fab_create_team.setOnClickListener {
			context?.let { ctx ->
				CreateTeamDialog.show(ctx) { name, passcode ->
					mEvent.id?.let { it1 ->
						mViewModel.createTeamToEvent(it1, name, passcode)
						Toasty.info(ctx, "Creating team ...").show()
					}
				}
			}
		}
	}

	/**
	 * Will store the event in global mEvent variable, in case it's null, Navigate back to Events
	 */
	private fun extractArguments() {
		val event = arguments?.getParcelable<Event>(EVENT_KEY)
		val joinEnabled = arguments?.getBoolean(JOIN_ENABLED_KEY)
		if (event != null && joinEnabled != null) {
			mEvent = event
			isJoinEnabled = joinEnabled
		} else {
			Log.e(TAG, "Null Event Passed", NullPointerException())
			findNavController().navigate(R.id.eventsFragment)
		}
	}


	companion object {
		val JOIN_ENABLED_KEY = "isUserPartOfTeam"
		val EVENT_KEY = "event"
	}
}
