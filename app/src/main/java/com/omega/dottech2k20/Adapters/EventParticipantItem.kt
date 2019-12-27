package com.omega.dottech2k20.Adapters

import com.omega.dottech2k20.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_participant.*

class EventParticipantItem(val content: String) : Item() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			tv_participant_name.text = content
		}
	}

	override fun getLayout(): Int {
		return R.layout.item_event_participant
	}

}