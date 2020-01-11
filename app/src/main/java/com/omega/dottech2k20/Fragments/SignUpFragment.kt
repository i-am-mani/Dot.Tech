package com.omega.dottech2k20.Fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.Utils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() {

	private val TAG: String = javaClass.simpleName
	lateinit var mActivity: MainActivity

	override fun onAttach(context: Context) {
		super.onAttach(context)

		try {
			mActivity = context as MainActivity
		} catch (e: ClassCastException) {
			e.printStackTrace()
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_sign_up, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		btn_sign_up.setOnClickListener { signUp(it) }
		btn_switch_to_sign_in.setOnClickListener { findNavController().navigate(R.id.signInFragment) }
	}

	fun signUp(view: View?) {
		if (isDataValid()) {
			progress_bar_sign_up?.visibility = View.VISIBLE
			val email = et_sign_up_email.text.toString()
			val password = et_sign_up_password1.text.toString()
			val fullName = et_sign_up_full_name.text.toString()
			val phone = et_phone_no.text.toString()
			AuthenticationUtils.registerNewUser(
				fullName,
				email,
				phone,
				password, this::onRegistrationCompletionCallBack
			)
		} else {
			Log.d(TAG, "signUp: Data is invalid")
			context?.let { Toasty.warning(it, "Data Provided is Invalid!").show() }
		}
	}

	private fun isDataValid(): Boolean {
		val fullname = et_sign_up_full_name.text.toString()
		val email = et_sign_up_email.text.toString()
		val password = et_sign_up_password1.text.toString()
		val rePassword = et_sign_up_password2.text.toString()
		val phone = et_phone_no.text.toString()

		return (validateFullName(fullname) &&
				validateEmail(email) &&
				validatePhoneNumber(phone) &&
				validatePasswords(password, rePassword))
	}

	private fun validatePhoneNumber(phone: String): Boolean {
		return when {
			phone.isEmpty() -> {
				input_layout_phone.error = "Phone number field cannot be left empty"
				false
			}
			!TextUtils.isDigitsOnly(phone) -> {
				input_layout_phone.error = "Please enter valid phone number"
				false
			}
			phone.length < 10 -> {
				input_layout_phone.error = "Please enter 10 digit valid phone number"
				false
			}
			else -> {
				input_layout_phone.error = ""
				true
			}
		}
	}

	/**
	 * Validate Passwords. Checks performed.
	 * 1) Not empty
	 * 2) both match
	 * 3) length greater then 6.
	 */
	private fun validatePasswords(password: String, rePassword: String): Boolean {
		when {
			password.isEmpty() -> {
				input_layout_password1.error = "Password field cannot be left empty"
				return false
			}
			password.length < 6 -> {
				input_layout_password1.error = "Password length cannot be shorter then 5 characters"
				return false
			}
			password != rePassword -> {
				input_layout_password1.error = "Passwords don't match!"
				input_layout_password2.error = "Passwords don't match!"
				return false
			}
			else -> {
				input_layout_password1.error = ""
				input_layout_password2.error = ""
				return true
			}
		}
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

	/**
	 * Validate full name. Checks performed
	 * 1) not empty
	 * 2) length greater then 4
	 */
	private fun validateFullName(fullname: String): Boolean {
		return when {
			fullname.isEmpty() -> {
				input_layout_fullname.error = "Name cannot be empty"
				false
			}
			fullname.length < 4 -> {
				input_layout_fullname.error = "Name is too short"
				false
			}
			else -> {
				input_layout_fullname.error = ""
				true
			}
		}
	}


	private fun onRegistrationCompletionCallBack(state: Boolean, exception: Exception?) {
		progress_bar_sign_up.visibility = View.INVISIBLE
		if (state) {
			progress_bar_sign_up.findNavController().popBackStack()
			progress_bar_sign_up.findNavController().navigate(R.id.eventsFragment)

			context?.let {
				Toasty.success(it, "Account has been created and Login Successful").show()
			}
			mActivity.setNavigationMenuItems()
		} else {
			context?.let {
				Toasty.error(it, "Failed to create account").show()
			}
		}
	}

}