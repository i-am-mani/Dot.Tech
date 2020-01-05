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
		// todo Check the structure of full name and strength of password
		val email = et_sign_up_email!!.text.toString()
		val password = et_sign_up_password1!!.text.toString()
		val rePassword = et_sign_up_password2.text.toString()
		val phone = et_phone_no.text.toString()
		return (!TextUtils.isEmpty(email)
				&& !TextUtils.isEmpty(password)
				&& !TextUtils.isEmpty(rePassword)
				&& Utils.isValidEmail(email)
				&& TextUtils.equals(password, rePassword)
				&& phone.length == 10)
	}

	private fun highlightEmptyFields() {}

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