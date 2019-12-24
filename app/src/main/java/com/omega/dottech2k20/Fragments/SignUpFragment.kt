package com.omega.dottech2k20.Fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.Utils
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
		btn_sign_up.setOnClickListener{signUp(it)}
	}

	fun signUp(view: View?) {

		progress_bar_sign_up?.visibility = View.VISIBLE

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
				if(progress_bar_sign_up != null){
					progress_bar_sign_up.visibility = View.INVISIBLE
					progress_bar_sign_up.findNavController().popBackStack()
					progress_bar_sign_up.findNavController().navigate(R.id.eventsFragment)
				}
				Toast.makeText(
					context,
					"Account has been created and Login Successful",
					Toast.LENGTH_SHORT
				).show()
				mActivity.setNavigationMenuItems()
			}
		}
	}

}