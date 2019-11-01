package com.omega.dottech2k20.Models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class UserEventViewModel(application: Application) : AndroidViewModel(application) {

	var mUserProfileLiveData: MutableLiveData<User> = MutableLiveData()
	var mEventsLiveData: MutableLiveData<List<Event>> = MutableLiveData()
	private val mFirestore = FirebaseFirestore.getInstance()
	private val mFireAuth = FirebaseAuth.getInstance()
	private val TAG: String = javaClass.simpleName

	fun getUserProfile(): LiveData<User>?{
		val user = mFireAuth.currentUser ?: return null
		val uid = user.uid

		// Fetch data from firestore only if
		if (mUserProfileLiveData.value == null) {
			fetchUserProfile(uid)
		}

		return mUserProfileLiveData

	}

	private fun fetchUserProfile(uid: String) {
		val document: DocumentReference = mFirestore.collection("Users").document(uid)
		document.addSnapshotListener{ snapshot,e ->
			if (e != null) {
				Log.w(TAG, "Listen failed.", e)
				return@addSnapshotListener
			}

			if (snapshot != null && snapshot.exists()) {
				mUserProfileLiveData.value = snapshot.toObject(User::class.java)
				Log.d(TAG, "Current data: ${mUserProfileLiveData.value.toString()}")

			} else {
				Log.d(TAG, "Current data: null")
			}

		}
	}

	fun getEvents(): LiveData<List<Event>>{
		val eventTask = mFirestore.collection("Events")

		eventTask.get().addOnCompleteListener {
			if(it.isSuccessful){
				val querySnapshot = it.result
				if (querySnapshot != null) {
					val events = querySnapshot.toObjects(Event::class.java)
					mEventsLiveData.value = events
				} else{
					Log.e(TAG, "getEvents : Empty Query Snapshot" )
				}
			} else{
				Log.e(TAG, "getEvents : Task Failed", it.exception )
			}
		}

		return mEventsLiveData
	}
}