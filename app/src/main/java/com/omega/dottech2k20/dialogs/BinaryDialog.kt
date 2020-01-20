package com.omega.dottech2k20.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.Utils

class BinaryDialog(
	val context: Context,
	val layoutId: Int = R.layout.dialog_binary
) {

	private val TAG = javaClass.simpleName

	var title: String = ""
	var description: String = ""

	var rightButtonId = R.id.btn_right
	var leftButtonId = R.id.btn_left

	var rightButtonName: String = "Confirm"
	var leftButtonName: String = "Cancel"

	var isRightButtonVisible = true
	var isLeftButtonVisible = true


	var rightButtonCallback: () -> Unit = { }
	var leftButtonCallback: () -> Unit = { }

	fun build() {
		val dialog = Dialog(context)
		dialog.setCanceledOnTouchOutside(true)

		dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS)
		dialog.setContentView(layoutId)
		dialog.window?.setLayout(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		)
		dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

		val tvTitle = dialog.findViewById<TextView>(R.id.text_title)
		val tvSubTitle = dialog.findViewById<TextView>(R.id.text_sub_title)

		val rightBtn = dialog.findViewById<Button>(rightButtonId)
		val leftBtn = dialog.findViewById<Button>(leftButtonId)

		setTitleAndDescription(tvTitle, tvSubTitle)

		setButtonVisibility(rightBtn, leftBtn)
		setButtonNames(rightBtn, leftBtn)

		setButtonCallbacks(rightBtn, dialog, leftBtn)

		dialog.show()
	}

	private fun setButtonVisibility(rightBtn: Button, leftBtn: Button) {
		if (!isRightButtonVisible) {
			rightBtn.visibility = View.GONE
		}
		if (!isLeftButtonVisible) {
			leftBtn.visibility = View.GONE
		}
	}

	private fun setButtonCallbacks(
		rightBtn: Button,
		dialog: Dialog,
		leftBtn: Button
	) {
		rightBtn.setOnClickListener {
			Utils.contextClickHapticFeedback(it)
			rightButtonCallback()
			dialog.dismiss()
		}

		leftBtn.setOnClickListener {
			leftButtonCallback()
			dialog.dismiss()
		}
	}

	private fun setButtonNames(rightBtn: Button, leftBtn: Button) {
		if (rightButtonName.isNotEmpty()) {
			rightBtn.text = rightButtonName
		}

		if (leftButtonName.isNotEmpty()) {
			leftBtn.text = leftButtonName
		}
	}

	private fun setTitleAndDescription(
		tvTitle: TextView,
		tvSubTitle: TextView
	) {
		if (title.isEmpty()) {
			tvTitle.visibility = View.GONE
		} else {
			tvTitle.text = title
		}

		if (description.isEmpty()) {
			tvSubTitle.visibility = View.GONE
		} else {
			tvSubTitle.text = description
		}
	}

}