package com.omega.dottech2k20.Adapters

import android.content.Context
import android.text.Html
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.Utils
import com.omega.dottech2k20.models.Event
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_details.*

class EventDetailItem(
	val context: Context,
	val event: Event,
	val participantsFragmentCallback: () -> Unit
) : Item() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		val endTime = event.endTime
		val startTime = event.startTime
		val title = event.title
		val description = event.longDescription

		if (title != null && description != null && endTime != null && startTime != null) {
			val dateString = Utils.getEventSchedule(startTime, endTime)

			viewHolder.apply {
				tv_event_title.text = title
				tv_date.text = dateString
				tv_event_details.text = Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)

				imbtn_view_participants.setOnClickListener {
					participantsFragmentCallback()
				}
			}
		}
	}

	override fun getLayout(): Int {
		return R.layout.item_event_details
	}


}