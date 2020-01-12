package com.omega.dottech2k20.dialogs

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
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
			val layoutName = findViewById<TextInputLayout>(R.id.input_layout_team_name)
			val layoutPass = findViewById<TextInputLayout>(R.id.input_layout_team_password)
			val confirm = findViewById<Button>(R.id.btn_confirm)
			val cancel = findViewById<Button>(R.id.btn_cancel)

			confirm.setOnClickListener {
				val name = etName.text.toString()
				val passcode = etPasscode.text.toString()

				if (validateTeamName(name, layoutName, context) && validatePassword(
						passcode,
						layoutPass,
						context
					)
				) {
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

	private fun validatePassword(
		passcode: String,
		layoutPass: TextInputLayout,
		context: Context
	): Boolean {
		return when {
			passcode.isEmpty() -> {
				layoutPass.error = "Passcode cannot be empty"
				false
			}
			passcode.length < 4 -> {
				layoutPass.error = "Passcode too short"
				Toasty.warning(context, "Pass should be at least 4 characters long").show()
				false
			}
			passcode.length > 20 -> {
				layoutPass.error = "Passcode too long!"
				false
			}
			passcode.length > 100 -> {
				Toasty.warning(context, "Ha Ha Ha, like i would fall for that!").show()
				false
			}
			else -> {
				layoutPass.error = ""
				true
			}
		}
	}

	private fun validateTeamName(
		name: String,
		layoutName: TextInputLayout,
		context: Context
	): Boolean {
		return when {
			name.isEmpty() -> {
				layoutName.error = "Team Name cannot be empty"
				false
			}
			name.length < 5 -> {
				layoutName.error = "Team Name cannot be shorter then 5 characters"
				false
			}
			!name.matches(Regex("^[a-zA-Z0-9]+(?:[_ -]?[a-zA-Z0-9])*\$")) -> {
				layoutName.error = "Invalid Name"
				Toasty.info(
					context, "Team Name must not consists of trailing or leading spaces" +
							"or use of any special characters(&,*,^,...)", Toast.LENGTH_LONG
				).show()
				false
			}
			else -> {
				layoutName.error = ""
				true
			}
		}
	}
}