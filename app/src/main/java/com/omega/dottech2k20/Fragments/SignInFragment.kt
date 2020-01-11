package com.omega.dottech2k20.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.R.layout
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.Utils
import com.omega.dottech2k20.dialogs.SingleTextFieldDialog
import com.omega.dottech2k20.models.UserEventViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment() {
	val TAG: String = javaClass.simpleName
	lateinit var mActivity: MainActivity
	lateinit var mViewModel: UserEventViewModel

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mViewModel = ViewModelProviders.of(mActivity).get(UserEventViewModel::class.java)
		return inflater.inflate(layout.fragment_sign_in, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		btn_sign_in.setOnClickListener { signInUser(it) }
		btn_forgot_password.setOnClickListener {
			context?.let { ctx ->
				SingleTextFieldDialog(ctx).apply {
					title = "Forgot Password"
					nameFieldHint = "Please enter email address of your account"
					hint = "E-Mail"
					minQueryFieldLines = 1
					queryType = "email"
					onSubmit = { name: String, query: String ->
						AuthenticationUtils.sendResetPasswordEmail(query) { exception: Exception? ->
							if (exception == null) {
								Toasty.success(ctx, "Reset Password email sent successfully").show()
								Toasty.info(ctx, "Do check your spam folder of your email provider")
							} else {
								Toasty.error(ctx, "Failed to send Reset Password Email").show()
							}
						}
					}
					build()
				}
			}
		}
	}

	fun signInUser(view: View?) {
		val email: String = et_login_email.text.toString()
		val password: String = et_login_password.text.toString()
		Log.d(TAG, "singInUser: name = " + email + "password " + password)
		if (isDataValid()) {
			progressbar_login.visibility = View.VISIBLE
			AuthenticationUtils.signInUser(
				email,
				password,
				this::onSignInCompletionCallback
			)
		} else {
			context?.let {
				Toasty.warning(it, "Invalid Data Provided").show()
			}
		}
	}

	private fun isDataValid(): Boolean {
		val email: String = et_login_email.text.toString()
		val password: String = et_login_password.text.toString()

		return (validateEmail(email) && validatePassword(password))
	}

	private fun validatePassword(password: String): Boolean {
		if (password.isEmpty()) {
			input_layout_password.error = "Please enter your password"
			return false
		}
		input_layout_password.error = ""
		return true
	}

	/**
	 * Validate email, checks performed
	 * 1) it's not emtpy
	 * 2) it conforms to email regex defined by Patterns lib.
	 */
	private fun validateEmail(email: String): Boolean {
		return when {
			email.isEmpty() -> {
				input_layout_email.error = "Email field cannot be left empty"
				false
			}
			!Utils.isValidEmail(email) -> {
				input_layout_email.error = "Please enter valid email address"
				false
			}
			else -> {
				input_layout_email.error = ""
				true
			}
		}
	}

	private fun onSignInCompletionCallback(state: Boolean, exception: Exception?) {
		progressbar_login.visibility = View.INVISIBLE
		if (state) {
			// Fetch the profile data before proceeding to events fragment.
			// If the data isn't refreshed, then userProfile/events stays null as no calls are made
			// after logging in.
			mViewModel.getUserProfile()?.observe(this, Observer {
				Log.i(TAG, "Login Successful")
				Snackbar.make(root_sign_in, "Login Successful", Snackbar.LENGTH_SHORT)
				mActivity.setNavigationMenuItems()
				progressbar_login.findNavController().popBackStack()
				progressbar_login.findNavController().navigate(R.id.eventsFragment)
			})
		} else {
			context?.let { Toasty.error(it, "Failed to login in").show() }
		}
	}

}