package com.omega.dottech2k20.Utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.omega.dottech2k20.Models.User

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
		fun createNewUser(user: FirebaseUser,id:String,fullName: String,email: String,phone: String,password: String,callback: (Boolean,Exception?)-> Unit) {
			val firestore = FirebaseFirestore.getInstance()
			val userObject = User(id, fullName, email, phone)
			val users: Task<Void> =
				firestore.collection("Users").document(id).set(userObject)

			users.addOnCompleteListener{
				if(it.isSuccessful){
					// Sign in User
					signInUser(email,password, callback)
				} else{
					Log.d(TAG, "AuthenticationUtils : Login Failed")
					user.delete() // Delete user since writing to FireStore failed
					callback(false,it.exception)
				}
			}
		}

		fun registerNewUser(fullName: String,email: String,phone: String,password: String,callback: (Boolean,Exception?)-> Unit) {
			val authTask: Task<AuthResult> = mAuth.createUserWithEmailAndPassword(email, password)
			authTask.addOnCompleteListener{
				if(it.isSuccessful){
					val result = it.result
					if(result!=null){
						val user = result.user // if user is null, no need to write to firestore
						if(user!= null){
							createNewUser(user,user.uid,fullName,email,phone,password,callback)
						}
					}
				} else{

					Log.d(TAG, "AuthenticationUtils : Login Failed")
					callback(false,it.exception)
				}
			}
		}


		fun signInUser(email: String,password: String,callback: (Boolean, Exception?) -> Unit) {
			val task: Task<AuthResult> = mAuth.signInWithEmailAndPassword(email, password)
			task.addOnCompleteListener{if(it.isSuccessful){
				callback(true,null)
			} else{
				callback(false,it.exception)
				Log.d(TAG, "AuthenticationUtils : Login Failed")
			}}
		}

		fun signOutUser() {
			mAuth.signOut()
		}

		fun deleteCurrentUser(callback: (Boolean,Exception?) -> Unit){
			val user = currentUser
			if (user != null) {
				user.delete().addOnCompleteListener{
					if(it.isSuccessful){
						callback(true,null)
					} else{
						callback(false,it.exception)
					}
				}
			}
		}

		fun verifyCurrentUserEmail(context: Context, listener: OnCompleteListener<Void>) {
			val currentUser = currentUser
			if (currentUser == null) {
				Log.d(TAG, "Current user is Null")
				return
			}

			currentUser.sendEmailVerification().addOnCompleteListener(context as Activity, listener)
		}

		val currentUser: FirebaseUser?
			get() = mAuth.currentUser
		}
}