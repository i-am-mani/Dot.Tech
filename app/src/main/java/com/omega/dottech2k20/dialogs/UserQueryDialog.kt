package com.omega.dottech2k20.dialogs

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import com.omega.dottech2k20.R
import kotlinx.android.synthetic.main.dialog_submit_text.*

class UserQueryDialog(val context: Context) {

	val layoutId = R.layout.dialog_submit_text
	private val TAG = javaClass.simpleName

	var title = ""
	var name = ""
	var hint = ""
	var onSubmit: (query: String) -> Unit = { Log.d(TAG, "Submit button clicked") }
	var headerIconId = R.drawable.ic_info_outline_white_24dp

	fun build() {
		val dialog = Dialog(context)
		dialog.setCanceledOnTouchOutside(true)

		dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS)
		dialog.setContentView(layoutId)
		dialog.window.setLayout(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		)
		dialog.window.setBackgroundDrawableResource(android.R.color.transparent)

		dialog.apply {
			tv_dialog_title.text = title
			et_user_name.setText(name)
			et_query.hint = hint
			im_dialog_header.setImageResource(headerIconId)
			btn_submit.setOnClickListener {
				onSubmit(et_query.text.toString())
				dismiss()
			}
		}
		dialog.show()
	}

}