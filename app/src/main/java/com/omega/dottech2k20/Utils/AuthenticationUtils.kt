package com.omega.dottech2k20.Utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest.Builder
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import java.util.*

class AuthenticationUtils {

	private val TAG = javaClass.simpleName

	fun verifyCurrentUserEmail(context: Context, listener: OnCompleteListener<Void>) {
		val currentUser = currentUser
		if(currentUser == null){
			Log.d(TAG, "Current user is Null")
			return
		}

		currentUser.sendEmailVerification().addOnCompleteListener(context as Activity, listener)
	}

	companion object {

		private val mAuth = FirebaseAuth.getInstance()
		private const val TAG = "AuthenticationUtils"

		/**
		 * Generates new user with given Credentials, It follows following pattern for user creation
		 * 		1. Store use data to firestore Users Collection
		 * 		2. Create new authenticated user with given email and password
		 */
		fun registerNewUser(fullName: String,email: String,phone: String,password: String,callback: (Boolean,Exception?)-> Unit) {
			val firestore = FirebaseFirestore.getInstance()
			val dataMap: MutableMap<String, String> = HashMap()
			dataMap["fullName"] = fullName
			dataMap["email"] = email
			dataMap["phone"] = phone

			val users: Task<DocumentReference> =
				firestore.collection("Users").add(dataMap)
			// 1
			users.addOnCompleteListener{
				if(it.isSuccessful){
					createNewUser(email,password,callback)
				} else{
					Log.d(TAG, "AuthenticationUtils : Login Failed")
					callback(false,it.exception)
				}
			}
		}

		private fun createNewUser(email: String, password: String, callback: (Boolean,Exception?) -> Unit) {
			val authTask: Task<AuthResult> = mAuth.createUserWithEmailAndPassword(email, password)
			authTask.addOnCompleteListener(getOnCompleteListener(callback))
		}

		private fun <T>getOnCompleteListener(callback : (Boolean, Exception?)-> Unit): OnCompleteListener<T> {
			return OnCompleteListener<T> {
				if(it.isSuccessful){
					callback(true,null)
				} else{
					callback(false,it.exception)
					Log.d(TAG, "AuthenticationUtils : Login Failed")
				}
			}
		}


		fun signInUser( context: Context, email: String,password: String,callback: (Boolean, Exception?) -> Unit) {
			val task: Task<AuthResult> = mAuth.signInWithEmailAndPassword(email, password)
			task.addOnCompleteListener(getOnCompleteListener(callback))
		}

		fun signOutUser() {
			mAuth.signOut()
		}

		fun deleteCurrentUser(callback: (Boolean,Exception?) -> Unit){
			val user = currentUser
			if (user != null) {
				user.delete().addOnCompleteListener(getOnCompleteListener(callback))
			}
		}

		val currentUser: FirebaseUser?
			get() = mAuth.currentUser
		}
}