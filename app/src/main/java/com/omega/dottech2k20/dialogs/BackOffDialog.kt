package com.omega.dottech2k20.dialogs

import android.content.Context

object BackOffDialog {

	/**
	 * Show BackOffDialog with Cool down time remaining.
	 *
	 * By defualt backoffTime is set to 10M
	 */
	fun show(context: Context, cooldownTime: Long, backOfTime: Long = 10) {
		BinaryDialog(context).apply {
			title = "Try again later \uD83D\uDE35"

			val backOffTimeString = getTimeString(backOfTime)
			val cooldownTimeString = getTimeString(cooldownTime)
			description =
				"It looks like you have joined and left the event in last $backOffTimeString.\n" +
						"Please wait and try again after ${cooldownTimeString}M"
			isLeftButtonVisible = false
			rightButtonName = "Close"
			leftButtonCallback = {}
			rightButtonCallback = {}
		}.build()
	}

	private fun getTimeString(time: Long): String {
		val hours = time / 60
		val minutes = time % 60
		var builder = ""

		if (hours != 0L) {
			builder += "$hours H "
		}
		if (minutes != 0L) {
			builder += "$minutes M"
		}
		return builder.trim()
	}
}