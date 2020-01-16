package com.omega.dottech2k20.dialogs

import android.content.Context
import com.omega.dottech2k20.Utils.AuthenticationUtils

object UnVerifiedEmailDialog {
	private val TAG = javaClass.simpleName

	fun show(c: Context) {
		val currentUser = AuthenticationUtils.currentUser
		if (currentUser != null) {
			BinaryDialog(c).apply {
				title = "Unverified Email"
				description =
					"Please verify your e-mail address.\n\n" +
							"Check your Inbox, or re-apply for verification"
				rightButtonName = "Verify"
				leftButtonName = "Close"
				rightButtonCallback = {
					currentUser.sendEmailVerification()
				}
			}.build()
		}

	}
}