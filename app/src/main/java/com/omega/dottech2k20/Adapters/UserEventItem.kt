package com.omega.dottech2k20.Adapters

import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.Utils
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_user_event.*


class UserEventItem(
	val event: Event,
	val leaveEventCallback: (event: Event) -> Unit,
	val viewDetailsCallback: (event: Event) -> Unit
) : Item() {


	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			tv_event_name.text = event.title
			val startTime = event.startTime
			val endTime = event.endTime

			if (startTime != null && endTime != null) {
				tv_event_datetime.text = Utils.getEventSchedule(startTime,endTime)
			}

			btn_event_leave.setOnClickListener { leaveEventCallback(event) }
			btn_event_details.setOnClickListener { viewDetailsCallback(event) }
		}
	}

	override fun getLayout() = R.layout.item_user_event


}