package com.omega.dottech2k20.dialogs

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.omega.dottech2k20.R
import es.dmoral.toasty.Toasty

object CreateTeamDialog {
	fun show(context: Context, callback: (name: String, passcode: String) -> Unit) {
		Dialog(context).apply {
			setCanceledOnTouchOutside(true)

			setContentView(R.layout.create_team_dialog)
			window.setLayout(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT
			)
			window.setBackgroundDrawableResource(android.R.color.transparent)

			val etName = findViewById<EditText>(R.id.et_team_name)
			val etPasscode = findViewById<EditText>(R.id.et_team_passcode)
			val confirm = findViewById<Button>(R.id.btn_confirm)
			val cancel = findViewById<Button>(R.id.btn_cancel)

			confirm.setOnClickListener {
				val name = etName.text.toString()
				val passcode = etPasscode.text.toString()
				if (name.isEmpty() || passcode.isEmpty()) {
					Toasty.warning(context, "You have left a field empty!").show()
				} else if (passcode.length <= 4) {
					Toasty.warning(
						context,
						"Password too short. It Should be at least 4 character long."
					).show()
				} else if (name.isNotEmpty() && passcode.isNotEmpty() && passcode.length >= 4) {
					callback(name.trim(), passcode)
					dismiss()
				}
			}

			cancel.setOnClickListener {
				dismiss()
			}
			show()
		}
	}
}