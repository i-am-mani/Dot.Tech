package com.omega.dottech2k20.Utils

import android.text.TextUtils
import android.util.Patterns

object Utils {
    val sharedPreferenceName: String
        get() = "dot.tech"
	fun isValidEmail(target: CharSequence?): Boolean {
		return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(
			target
		).matches()
	}

}

