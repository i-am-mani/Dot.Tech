package com.omega.dottech2k20.Models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class UserEventViewModel(application: Application) : AndroidViewModel(application) {

	var mUserProfileLiveData: MutableLiveData<User> = MutableLiveData()
	var mEventsLiveData: MutableLiveData<List<Event>> = MutableLiveData()
	private val mFirestore = FirebaseFirestore.getInstance()
	private val mFireAuth = FirebaseAuth.getInstance()
	private val TAG: String = javaClass.simpleName

	fun getUserProfile(): LiveData<User>? {
		val user = mFireAuth.currentUser ?: return null
		val uid = user.uid

		if (mUserProfileLiveData.value == null) {
			addUserProfileSnapShotListener(uid)
		}
		return mUserProfileLiveData

	}

	private fun addUserProfileSnapShotListener(uid: String) {
		val document: DocumentReference = mFirestore.collection("Users").document(uid)
		document.addSnapshotListener { snapshot, e ->
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

	fun getEvents(): LiveData<List<Event>> {
		val eventTask = mFirestore.collection("Events")
		if (mEventsLiveData.value == null) {
			addEventsSnapShotListener(eventTask)
		}
		return mEventsLiveData
	}

	private fun addEventsSnapShotListener(eventTask: CollectionReference) {
		eventTask.addSnapshotListener { snapshot, exception ->
			if (exception != null) {
				Log.w(TAG, "Listen failed.", exception)
				return@addSnapshotListener
			}

			if (snapshot != null) {
				mEventsLiveData.value = snapshot.toObjects(Event::class.java)
				Log.d(TAG, "Current data: ${mEventsLiveData.value.toString()}")
			} else {
				Log.d(TAG, "Current data: null")
			}
		}
	}

	fun joinEvent(event: Event) {

		var user: User? = mUserProfileLiveData.value

		if (user == null) {
			val uid = mFireAuth.currentUser?.uid ?: return

			val document: DocumentReference = mFirestore.collection("Users").document(uid)
			document.get().addOnCompleteListener {
				if (it.isSuccessful) {
					// Check of nullability
					addUserInEvent(event, it.result?.toObject(User::class.java)!!)
				} else {
					Log.e(TAG, "Failed To get User : ", it.exception)
				}
			}
		} else {
			addUserInEvent(event, user)
		}


	}

	private fun addUserInEvent(
		event: Event,
		user: User
	) {
		// Inside Sub-collection ( Participants ), set the id of participant document as UID
		// done to easily retrieve the document
		val eventParticipantsDoc = mFirestore
			.collection("Events")
			.document(event.id!!).collection("Participants").document(user.id!!)

		val userEvents = mFirestore.collection("Users").document(user.id!!)

		mFirestore.runBatch {
			it.set(eventParticipantsDoc, user)
			it.update(userEvents, FieldPath.of("events"), FieldValue.arrayUnion(event.id))
		}.addOnCompleteListener {
			if (it.isSuccessful) {
				Log.d(TAG, "Joining Events Successful")
			} else {
				Log.d(TAG, "Joining Events Failed")
			}
		}
	}

	fun unjoinEvents(event: Event) {

		var user: User? = mUserProfileLiveData.value

		if (user == null) {
			val uid = mFireAuth.currentUser?.uid ?: return

			val document: DocumentReference = mFirestore.collection("Users").document(uid)
			document.get().addOnCompleteListener {
				if (it.isSuccessful) {
					removeUserFromEvent(event, it.result?.toObject(User::class.java)!!)
				} else {
					Log.e(TAG, "Failed To get User : ", it.exception)
				}
			}
		} else {
			removeUserFromEvent(event, user)
		}
	}

	private fun removeUserFromEvent(
		event: Event,
		user: User
	) {
		val eventParticipantsDoc = mFirestore
			.collection("Events")
			.document(event.id!!).collection("Participants").document(user.id!!)

		val userEvents = mFirestore.collection("Users").document(user.id!!)

		mFirestore.runBatch {
			it.delete(eventParticipantsDoc)
			it.update(userEvents, FieldPath.of("events"), FieldValue.arrayRemove(event.id))
		}.addOnCompleteListener {
			if (it.isSuccessful) {
				Log.d(TAG, "Joining Events Successful")
			} else {
				Log.d(TAG, "Joining Events Failed")
			}
		}
	}

}