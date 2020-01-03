package com.omega.dottech2k20.dialogs

import android.content.Context
import androidx.navigation.NavController
import com.omega.dottech2k20.R

object RequestForLoginDialog {
	fun show(context: Context, navController: NavController) {
		BinaryDialog(context).apply {
			title = "Unsigned user"
			description = "Please Sign in or Sign Up to Continue"
			leftButtonName = "Sign In"
			rightButtonName = "Sign Up"
			rightButtonId = R.id.btn_right
			leftButtonId = R.id.btn_left
			leftButtonCallback = { navController.navigate(R.id.signInFragment) }
			rightButtonCallback = { navController.navigate(R.id.signUpFragment) }
		}.build()
	}
}