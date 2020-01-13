package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omega.dottech2k20.Adapters.HeaderCountItem
import com.omega.dottech2k20.Adapters.TeamItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.SharedPreferenceUtils
import com.omega.dottech2k20.dialogs.BackOffDialog
import com.omega.dottech2k20.dialogs.CreateTeamDialog
import com.omega.dottech2k20.dialogs.SingleTextFieldDialog
import com.omega.dottech2k20.models.Event
import com.omega.dottech2k20.models.Team
import com.omega.dottech2k20.models.Teammate
import com.omega.dottech2k20.models.UserEventViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_event_teams.*


class EventTeamsFragment : Fragment() {

	private var isReadOnly: Boolean? = null
	private val TAG = javaClass.simpleName
	private lateinit var mActivity: MainActivity
	private lateinit var mAdapter: GroupAdapter<GroupieViewHolder>
	private lateinit var mViewModel: UserEventViewModel
	private lateinit var mEvent: Event
	private lateinit var mTeamSection: Section
	private lateinit var mDataSet: List<Team>
	private var isUserPartOfTeam: Boolean? = null

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mAdapter = GroupAdapter()
		mTeamSection = Section()
		setTeamsHeader(0)
		mAdapter.add(mTeamSection)
		extractArguments()
		return inflater.inflate(R.layout.fragment_event_teams, container, false)
	}

	private fun setTeamsHeader(count: Int) {
		mTeamSection.setHeader(HeaderCountItem("Total Teams", count))
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
		updateFAB()
		addSearchCallbacks()
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(UserEventViewModel::class.java)
		if (AuthenticationUtils.currentUser != null) {
			mViewModel.getUserProfile()?.observe(this, Observer { user ->
				if (user != null) {
					isUserPartOfTeam = user.events.find {
						it.eventId == mEvent.id
					} != null
					setTeamsObserver()
					updateFAB()
				}
			})
		} else {
			isUserPartOfTeam = false
			setTeamsObserver()
			updateFAB()
		}
	}

	private fun updateFAB() {
		if (isUserPartOfTeam == true || isReadOnly == true) {
			fab_create_team.hide()
		} else {
			fab_create_team.show()
		}
	}

	private fun setTeamsObserver() {
		mEvent.id?.let { eid ->
			mViewModel.getTeamsOfEvent(eid).observe(this, Observer { teams ->
				if (teams != null && teams.count() > 0) {
					val eventTeams = teams[eid]
					if (eventTeams != null) {
						populateAdapter(eventTeams)
						mDataSet = eventTeams
					}
				}
			})
		}
	}

	private fun populateAdapter(eventTeams: List<Team>) {
		val data = getSortedData(eventTeams.toMutableList())
		if (mAdapter.itemCount == 0) {
			mTeamSection.addAll(getTeamItems(data))
		} else {
			mTeamSection.update(getTeamItems(data))
		}
		setTeamsHeader(data.count())
	}

	private fun getSortedData(eventTeams: MutableList<Team>): List<Team> {
		if (isUserPartOfTeam == true) {
			val userTeamIndex = eventTeams.indexOfFirst { team ->
				team.teammates.filter { it.id == AuthenticationUtils.currentUser?.uid }.size == 1
			}
			if (userTeamIndex != -1) {
				val userTeam = eventTeams[userTeamIndex]
				eventTeams.removeAt(userTeamIndex)
				eventTeams.add(0, userTeam)
			}
		}
		return eventTeams
	}

	private fun getTeamItems(eventTeams: List<Team>): List<TeamItem> {
		val listOfItems = mutableListOf<TeamItem>()
		val teamSize = mEvent.teamSize
		isUserPartOfTeam?.let { isJoin ->
			if (teamSize != null) {
				context?.let { ctx ->
					for (team in eventTeams) {
						val teamItem = TeamItem(
							ctx,
							team,
							isJoin,
							isReadOnly ?: false,
							teamSize,
							::deleteTeam,
							::removeTeammate,
							::joinTeam
						)
						listOfItems.add(teamItem)
					}
				}
			} else {
				Log.e(TAG, "Team size is Null", NullPointerException("teamSize"))
			}
		}
		return listOfItems
	}

	private fun removeTeammate(team: Team, teammate: Teammate) {
		val eid = mEvent.id
		val tid = team.id
		val teammateId = teammate.id
		if (eid != null && tid != null && teammateId != null) {
			mViewModel.removeTeammate(eid, tid, teammate)
			// If User leaves the event then set timestamp to follow back-off strategy
			setEventLeaveTimestamp(teammate.id)
		}
	}

	/**
	 * Stores EventId with Timestamp in SP
	 */
	private fun setEventLeaveTimestamp(teammateId: String) {
		AuthenticationUtils.currentUser?.let { user ->
			if (user.uid == teammateId) {
				SharedPreferenceUtils.registerTimeStamp(context, mEvent.id)
			}
		}
	}

	/**
	 * Deletes the team passed as param, And Stores Timestamp with team_eventId as label. Used for
	 * preventing team creation within defined time period.
	 */
	private fun deleteTeam(team: Team) {
		context?.let { ctx ->
			mEvent.id?.let { eventId ->
				mViewModel.deleteTeamFromEvent(team, eventId)
				SharedPreferenceUtils.registerTimeStamp(ctx, getSharedPreferenceIdForTeam(eventId))
			}
		}
	}

	/**
	 * Adds currently logged in user to the team.
	 *
	 * Prevents Joining Team if the user has left the some team in the same event in given back off
	 * period.
	 */
	private fun joinTeam(team: Team) {
		val tid = team.id
		val teamName = team.name
		val passcode = team.passcode
		if (tid != null && teamName != null && passcode != null) {
			context?.let { ctx ->
				val validBackoff = SharedPreferenceUtils.isValidBackoff(context, mEvent.id)
				if (!validBackoff) {
					SingleTextFieldDialog(ctx).apply {
						title = "Join Team?"
						name = team.name
						minQueryFieldLines = 1
						hint = "Passcode"
						queryType = "password"
						onSubmit = { name: String, query: String ->
							if (query == team.passcode) {
								mViewModel.joinTeam(mEvent, tid)
							} else {
								Toasty.warning(ctx, "Incorrect Password...").show()
							}
						}
						build()
					}
				} else {
					BackOffDialog.show(
						ctx,
						SharedPreferenceUtils.getBackOffTime(context, mEvent.id)
					)
				}
			}
		}
	}

	private fun addFABCallback() {
		fab_create_team.setOnClickListener {
			context?.let { ctx ->

				val validBackoff =
					SharedPreferenceUtils.isValidBackoff(
						ctx,
						getSharedPreferenceIdForTeam(mEvent.id),
						TEAM_COOLDOWN
					)
				if (!validBackoff) {
					showTeamCreationDialog(ctx)
				} else {
					BackOffDialog.show(
						ctx,
						SharedPreferenceUtils.getBackOffTime(
							context,
							getSharedPreferenceIdForTeam(mEvent.id),
							TEAM_COOLDOWN
						),
						TEAM_COOLDOWN
					)
				}
			}
		}
	}

	private fun showTeamCreationDialog(ctx: Context) {
		CreateTeamDialog.show(ctx) { name, passcode ->
			mEvent.id?.let { eventId ->
				mViewModel.createTeamToEvent(eventId, name, passcode)
			}
		}
	}

	/**
	 * Will store the event in global mEvent variable, in case it's null, Navigate back to Events
	 */
	private fun extractArguments() {
		val event = arguments?.getParcelable<Event>(EVENT_KEY)
		val readOnly = arguments?.getBoolean(READ_ONLY) ?: false
		if (event != null) {
			mEvent = event
			isReadOnly = readOnly
		} else {
			Log.e(TAG, "Null Event Passed", NullPointerException())
			findNavController().navigate(R.id.eventsFragment)
		}
	}

	// Search Functionality

	private fun addSearchCallbacks() {
		fab_search.setOnClickListener {
			search_team.visibility = VISIBLE
			search_team.isIconified = false
			fab_search.hide()
		}

		search_team.setOnCloseListener {
			search_team.visibility = GONE
			fab_search.show()
			true
		}

		search_team.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				return false
			}

			override fun onQueryTextChange(newText: String?): Boolean {
				val newDataSet = mDataSet.filter {
					val teamName = it.name
					return@filter if (newText != null && teamName != null) {
						teamName.contains(newText, true)
					} else {
						false
					}
				}
				mTeamSection.update(getTeamItems(newDataSet))
				return true
			}
		})
	}


	companion object {
		val READ_ONLY = "readOnly"
		val EVENT_KEY = "event"
		val TEAM_COOLDOWN: Long = 12 * 60 // 12 hours in minutes
		val getSharedPreferenceIdForTeam = { id: String? ->
			"team_$id"
		}
	}
}
