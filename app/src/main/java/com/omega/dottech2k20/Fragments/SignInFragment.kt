package com.omega.dottech2k20.Fragments

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.omega.dottech2k20.R
import com.omega.dottech2k20.R.layout
import com.omega.dottech2k20.Utils.AuthenticationUtils
import kotlinx.android.synthetic.main.fragment_sign_in.*
import java.time.Duration

class SignInFragment : Fragment() {
	val TAG: String = javaClass.simpleName

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(layout.fragment_sign_in, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		btn_sign_in.setOnClickListener{signInUser(it)}
	}

	fun signInUser(view: View?) {
		progressbar_login.visibility = View.VISIBLE
		val email: String = et_login_email.text.toString()
		val password: String = et_login_password.text.toString()
		Log.d(TAG, "singInUser: name = " + email + "password " + password)
		if (isDataValid()) {
			AuthenticationUtils.signInUser(
				context!!,
				email,
				password,
				this::onSignInCompletionCallback
			)
		}
	}

	fun switchToSignUp(view: View?) {}

	private fun isDataValid(): Boolean {
		val email: String = et_login_email.text.toString()
		val password: String = et_login_password.text.toString()
		return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
	}

	private fun onSignInCompletionCallback(state: Boolean,exception: Exception?){
		progressbar_login.visibility = View.INVISIBLE
		if(state){
			Log.d(TAG, "Login Successful")

			Snackbar.make(et_login_email,"Login Successful",Snackbar.LENGTH_SHORT)

			progressbar_login.findNavController().navigate(R.id.eventsFragment)
		} else{
			Log.d(TAG, "Login Unsuccessful")
		}
	}

}