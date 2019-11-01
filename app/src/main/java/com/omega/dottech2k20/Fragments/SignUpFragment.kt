package com.omega.dottech2k20.Fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.Utils
import kotlinx.android.synthetic.main.fragment_sign_up.*
import java.lang.Exception

class SignUpFragment : Fragment() {

	private val TAG: String = javaClass.simpleName

	override fun onAttach(context: Context) {
		super.onAttach(context)
		try {
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
		btn_sign_up.setOnClickListener{signUp(it)}
	}

	fun signUp(view: View?) {

		progressBar?.visibility = View.VISIBLE

		if (isDataValid()) {
			val email = et_sign_up_email.text.toString()
			val password = et_sign_up_password1.text.toString()
			val fullName = et_sign_up_full_name.text.toString()
			val phone = et_phone_no.text.toString()
			AuthenticationUtils.registerNewUser(
				fullName,
				email,
				phone,
				password, this::onRegistrationCompletionCallBack)
		} else {
			Log.d(TAG, "signUp: Data is invalid")
			Toast.makeText(
				context,
				"Data Provided is Invalid!",
				Toast.LENGTH_SHORT
			).show()
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

	private fun onRegistrationCompletionCallBack(state: Boolean, exception: Exception?){
		when(state){
			true -> {
				progressBar.visibility = View.INVISIBLE
				Toast.makeText(
					context,
					"Account has been created and Login Successful",
					Toast.LENGTH_SHORT
				).show()
				progressBar.findNavController().navigate(R.id.eventsFragment)
				// Change the navigation view menu's
			}
		}
	}

}