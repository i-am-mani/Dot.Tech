package com.omega.dottech2k20.dialogs

import android.content.Context
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.SharedPreferenceUtils

object LeaveEventDialog {
	fun show(context: Context, event: Event, viewModel: UserEventViewModel) {
		BinaryDialog(context, R.layout.dialog_event_confirmation).apply {
			title = "Leave this Event ?"
			rightButtonCallback = {
				viewModel.unjoinEvents(event)
				SharedPreferenceUtils.registerTimeStamp(context, event.id)
			}
			leftButtonCallback = { }
		}.build()
	}

}