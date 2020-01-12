package com.omega.dottech2k20.Utils

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.view.View
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

object Utils {
    val sharedPreferenceName: String
        get() = "dot.tech"
	fun isValidEmail(target: CharSequence?): Boolean {
		return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(
			target
		).matches()
	}

	/**
	 * Returns date in from of "Sun, 23 Feb  12:30 - 02:30"
	 * Useful for representing time range on same day
	 */
	fun getEventSchedule(
		startTime: Timestamp,
		endTime: Timestamp
	): String {
		val dateFormatter = SimpleDateFormat("EEE, d MMM")
		val timeFormatter = SimpleDateFormat("hh:mm")
		val date = startTime.toDate()
		val eventDate = dateFormatter.format(date)

		val eventStartTime = timeFormatter.format(date)
		val eventEndTime = timeFormatter.format(endTime.toDate())
		val time = "$eventDate    $eventStartTime - $eventEndTime"

		return time
	}

	/**
	 * Returns date time formatted in form of "Tue, 12 Nov 2019 03.00"
	 */
	fun getFormattedTime(timestamp: Timestamp): String {
		val dateFormatter = SimpleDateFormat("EEE, d MMM YYYY")
		val timeFormatter = SimpleDateFormat("hh:mm")
		val date = timestamp.toDate()
		return "${dateFormatter.format(date)}   ${timeFormatter.format(date)}"
	}

	fun convertDPtoPX(context: Context, dp: Int): Int {
		val px = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP,
			dp.toFloat(),
			context.resources.displayMetrics
		)
		return px.toInt()

	}

	fun contextClickHapticFeedback(view: View) {
		view.isHapticFeedbackEnabled = true
		view.performHapticFeedback(
			HapticFeedbackConstants.CONTEXT_CLICK,
			HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
		)
	}

	fun virtualClickHapticFeedback(view: View) {
		view.isHapticFeedbackEnabled = true
		view.performHapticFeedback(
			HapticFeedbackConstants.VIRTUAL_KEY,
			HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
		)
	}

}

