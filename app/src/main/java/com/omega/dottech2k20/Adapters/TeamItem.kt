package com.omega.dottech2k20.Adapters

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.models.Team
import com.omega.dottech2k20.models.Teammate
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.item_team.*
import kotlinx.android.synthetic.main.item_teammate.*

/**
 *  Responsible for a populating individual team and callback on it.
 *  1) Remove Teammate (Only to Creator)
 *  2) Remove self (Only to logged in user)
 *  3) Delete Team (Only if user is the creator and User tries to leave the Team)
 *  4) Join Team (Any valid user - Verified email)
 */
class TeamItem(
	val context: Context,
	val mTeam: Team,
	val onDeleteTeamCallback: (teamId: String) -> Unit,
	private val onRemoveTeammateCallback: (teammate: Teammate) -> Unit,
	val onJoinTeamCallback: (teamId: String) -> Unit
) : Item() {

	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			val (id, name, creator, passcode, teammates) = mTeam
			if (id != null && name != null && creator != null && passcode != null && teammates != null) {
				tv_team_name.text = name
				imbtn_remove_team.setOnClickListener {
					// TODO Show warning Dialog
					onDeleteTeamCallback(id)
				}
				btn_join.setOnClickListener {
					onJoinTeamCallback(id)
				}
				initTeammatesRV(viewHolder)
			} else {
				Toasty.info(context, "NO Teams to display")
				// Change visibility of card
			}
		}
	}

	private fun initTeammatesRV(viewHolder: GroupieViewHolder) {
		val adapter = GroupAdapter<com.xwray.groupie.GroupieViewHolder>()
		adapter.addAll(getTeammateItems())
		viewHolder.apply {
			rv_teammates.adapter = adapter
			rv_teammates.layoutManager =
				GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
		}

	}

	private fun getTeammateItems(): List<TeammateItem> {
		val listOfItems = mutableListOf<TeammateItem>()
		for (teammate in mTeam.teammates) {
			val creatorsId = mTeam.creator
			if (creatorsId != null) {
				val item = TeammateItem(teammate, creatorsId, onRemoveTeammateCallback)
				listOfItems.add(item)
			}
		}
		return listOfItems
	}

	override fun getLayout(): Int {
		return R.layout.item_team
	}

	class TeammateItem(
		private val teammate: Teammate,
		private val creatorsId: String,
		val onRemoveTeammateCallback: (teammate: Teammate) -> Unit
	) : Item() {
		override fun bind(viewHolder: GroupieViewHolder, position: Int) {
			val user = AuthenticationUtils.currentUser
			viewHolder.apply {
				val (id, name) = teammate
				if (id != null && name != null && user != null) {
					tv_teammate_name.text = name

					if (user.uid == creatorsId || user.uid == id) {
						imbtn_remove_teammate.visibility = View.VISIBLE
						imbtn_remove_teammate.setOnClickListener { onRemoveTeammateCallback(teammate) }
					}
				}

			}
		}

		override fun getLayout(): Int {
			return R.layout.item_teammate
		}
	}

}