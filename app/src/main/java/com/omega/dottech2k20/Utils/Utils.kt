package com.omega.dottech2k20.Utils

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.util.TypedValue
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

	fun getEventSchedule(
		startTime: Timestamp,
		endTime: Timestamp
	): String {
		val dateFormatter = SimpleDateFormat("MMM, EEE")
		val timeFormatter = SimpleDateFormat("hh:mm")
		val date = startTime.toDate()
		val eventDate = dateFormatter.format(date)

		val eventStartTime = timeFormatter.format(date)
		val eventEndTime = timeFormatter.format(endTime.toDate())
		val time = "$eventDate    $eventStartTime - $eventEndTime"

		return time
	}

	fun getFormattedTime(timestamp: Timestamp): String {
		val dateFormatter = SimpleDateFormat("MMM, EEE")
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

}

