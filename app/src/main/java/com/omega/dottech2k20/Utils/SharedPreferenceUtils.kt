package com.omega.dottech2k20.Utils

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log

/**
 *  Helper class to Read/Write to Shard Preferences. Specifically used for managing BackOfTime.
 *
 * Note :- BackOfTime is in minutes the default is set to 10 minutes.
 */
object SharedPreferenceUtils {
	private val TAG = javaClass.simpleName

	fun getBackOffTime(context: Context?, id: String?, backOfTime: Long = 10): Long {
		return if (context != null && id != null) {
			val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
			val lastRegisteredTimestamp = sharedPreference.getLong(id, 0)
			val diff = System.currentTimeMillis() - lastRegisteredTimestamp
			backOfTime - (diff / (1000 * 60))
		} else {
			0
		}
	}

	fun registerTimeStamp(context: Context?, id: String?) {
		if (context != null && id != null) {
			// Whenever user leaves event, mark the event against timestamp
			// Prevent user from joining for next defined time interval - to prevent spamming
			val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
			val editor = sharedPreference.edit()
			editor.putLong(id, System.currentTimeMillis())
			editor.apply()
		} else {
			Log.e(TAG, ": Context or id is null", NullPointerException())
		}
	}

	fun removeTimestamp(context: Context?, id: String?) {
		if (context != null && id != null) {
			// Whenever user leaves event, mark the event against timestamp
			// Prevent user from joining for next defined time interval - to prevent spamming
			val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
			val editor = sharedPreference.edit()
			editor.remove(id)
			editor.apply()
		} else {
			Log.e(TAG, "Context or id is null", NullPointerException())
		}
	}

	/**
	 * Return true if user join same event within defined time interval.
	 */
	fun isValidBackoff(context: Context?, id: String?, backOfTime: Long = 10): Boolean {
		// Check if the user hasn't left the same event since last 10 mins
		// To avoid Spamming, and overloading db with constant queries
		val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
		val lastRegisteredTimestamp = sharedPreference.getLong(id, 0)
		return (System.currentTimeMillis() - lastRegisteredTimestamp) <= (backOfTime * 60 * 1000)
	}
}