package com.omega.dottech2k20.dialogs

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.R

object JoinEventConfirmationDialog {
	fun show(context: Context, event: Event, viewModel: UserEventViewModel) {
		Dialog(context).apply {
			setCanceledOnTouchOutside(true)

			setContentView(R.layout.dialog_join_event_confirmation)
			window.setLayout(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
			)
			window.setBackgroundDrawableResource(android.R.color.transparent)

			val confirmBtn = findViewById<Button>(R.id.btn_right)
			val cancelBtn = findViewById<Button>(R.id.btn_left)
			val chbxAnonUser = findViewById<CheckBox>(R.id.chbx_anon_user)

			confirmBtn.setOnClickListener {
				val isAnonymous = chbxAnonUser.isChecked
				viewModel.joinEvent(event, isAnonymous)
				dismiss()
			}

			cancelBtn.setOnClickListener {
				dismiss()
			}
		}.show()
	}
}