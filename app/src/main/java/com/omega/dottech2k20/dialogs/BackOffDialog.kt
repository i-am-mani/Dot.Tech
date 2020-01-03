package com.omega.dottech2k20.dialogs

import android.content.Context

object BackOffDialog {

	fun show(context: Context, time: Long) {
		BinaryDialog(context).apply {
			title = "Try again later \uD83D\uDE35"
			description =
				"It looks like you have joined and left the event in last 10 minutes.\n" +
						"Please wait and try again after ${time}M"
			isLeftButtonVisible = false
			rightButtonName = "Close"
			leftButtonCallback = {}
			rightButtonCallback = {}
		}.build()
	}
}