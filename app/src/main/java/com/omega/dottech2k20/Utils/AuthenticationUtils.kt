package com.omega.dottech2k20.Utils

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.omega.dottech2k20.models.User

class AuthenticationUtils {

	private val TAG = javaClass.simpleName

	companion object {

		private val mAuth = FirebaseAuth.getInstance()
		private const val TAG = "AuthenticationUtils"

		/**
		 * Generates new user with given Credentials, It follows following pattern for user creation
		 * 		1. Create new authenticated user with given email and password
		 * 		2. Store use data to firestore Users Collection
		 *
		 *	@param user  The FirebaseUser Object, to delete the created user in case writing to Firestore fails
		 *  @param fullName full name of the user
		 *  @param email email for the user
		 *  @param password password used to sign in user
		 *  @param callback Function would be executed when the process finishes successfully or fails
		 *
		 */
		private fun createNewUser(
			user: FirebaseUser,
			id: String,
			fullName: String,
			email: String,
			phone: String,
			password: String,
			callback: (Boolean, Exception?) -> Unit
		) {
			val firestore = FirebaseFirestore.getInstance()
			val userObject = User(id, fullName, email, phone)
			val users: Task<Void> =
				firestore.collection("Users").document(id).set(userObject)

			users.addOnCompleteListener{
				if(it.isSuccessful){
					// Sign in User
					signInUser(email,password, callback)
				} else{
					user.delete() // Delete user since writing to FireStore failed
					callback(false,it.exception)
				}
			}
		}

		/**
		 * Creates user with given credentials and send email verification mail
		 */
		fun registerNewUser(fullName: String, email: String, phone: String, password: String, callback: (Boolean, Exception?)-> Unit) {
			val authTask: Task<AuthResult> = mAuth.createUserWithEmailAndPassword(email, password)
			authTask.addOnCompleteListener{
				if(it.isSuccessful){
					val result = it.result
					if(result!=null){
						val user = result.user // if user is null, no need to write to firestore
						if(user!= null){
							createNewUser(user,user.uid,fullName,email,phone,password,callback)
							user.sendEmailVerification()
						}
					}
				} else{
					callback(false,it.exception)
				}
			}
		}

		/**
		 * Signs in User with provided details.
		 *
		 * @param email User email
		 * @param password User password
		 * @param callback Function with Boolean as first parameter indicating success or failure
		 * 					Exception is seconds parameter, if any.
		 */
		fun signInUser(email: String,password: String,callback: (Boolean, Exception?) -> Unit) {
			val task: Task<AuthResult> = mAuth.signInWithEmailAndPassword(email, password)
			task.addOnCompleteListener{if(it.isSuccessful){
				callback(true,null)
			} else{
				callback(false,it.exception)
			}}
		}

		fun signOutUser() {
			mAuth.signOut()
		}


		fun sendResetPasswordEmail(email: String, callback: (Exception?) -> Unit) {
			mAuth.sendPasswordResetEmail(email).addOnCompleteListener {
				if (it.isSuccessful) {
					callback(null)
				} else {
					callback(it.exception)
				}
			}
		}

		val currentUser: FirebaseUser?
			get() = mAuth.currentUser
		}
}