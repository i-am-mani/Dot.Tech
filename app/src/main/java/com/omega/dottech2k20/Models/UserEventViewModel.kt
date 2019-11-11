package com.omega.dottech2k20.Models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class UserEventViewModel(application: Application) : AndroidViewModel(application) {

	private var mUserProfileLiveData: MutableLiveData<User> = MutableLiveData()
	private var mEventsLiveData: MutableLiveData<List<Event>> = MutableLiveData()
	private var mUserEventsLiveData: MediatorLiveData<List<Event>>? = null
	private lateinit var mGlobalNotificationsLiveData: MutableLiveData<List<Notification>>
	private lateinit var mUserNotificationsLiveData: MutableLiveData<List<Notification>>
	private lateinit var mNotificationsLiveData: MediatorLiveData<List<Notification>>
	private val mFirestore = FirebaseFirestore.getInstance()
	private val mFireAuth = FirebaseAuth.getInstance()
	private val TAG: String = javaClass.simpleName

	fun getUserProfile(): LiveData<User>? {
		val user = mFireAuth.currentUser ?: return null
		val uid = user.uid

		Log.d(TAG, "Initial value  = ${mUserProfileLiveData.value}")
		val document: DocumentReference = mFirestore.collection("Users").document(uid)
		if (mUserProfileLiveData.value == null) {
			addDocumentSnapShotListener(document) {
				mUserProfileLiveData.value = it.toObject(User::class.java)
			}
		}
		return mUserProfileLiveData

	}

	/**
	 * 	Update User data in Users Collection and All the User data instances in Participant collection.
	 *
	 * 	Note :- the data is being replaced directly
	 */

	fun updateUserProfile(user: User) {
		mFirestore.runBatch { batch ->
			user.id?.let { userId ->
				// re-set User (Update user)
				val userDoc = mFirestore.collection("Users").document(userId)
				batch.set(userDoc, user)

				val userEventIds = user.events
				if (userEventIds != null) {
					for (eventIds in userEventIds) {
						// Events -> Participant -> user_doc (Update user_doc)
						val eventParticipantDoc = mFirestore.collection("Events")
							.document(eventIds).collection("Participants").document(userId)
						batch.set(eventParticipantDoc, user)
					}
				}
			}
		}
	}


	/**
	 * Attaches snapshot listener to document reference, and executes provided callback whenever
	 * the snapshot listener is triggered ( due to change in data etc..)
	 */
	private fun addDocumentSnapShotListener(
		doc: DocumentReference,
		callback: (snapshot: DocumentSnapshot) -> Unit
	) {
		doc.addSnapshotListener { snapshot, e ->
			when {
				e != null -> {
					Log.w(TAG, "Listen failed.", e)
					return@addSnapshotListener
				}
				snapshot != null && snapshot.exists() -> callback(snapshot)
				else -> Log.d(TAG, "Current data: null")
			}

		}
	}

	fun getEvents(): LiveData<List<Event>> {
		val eventTask = mFirestore.collection("Events")

		if (mEventsLiveData.value == null) {
			addQuerySnapShotListener(eventTask) {
				mEventsLiveData.value = it.toObjects(Event::class.java)
				Log.d(TAG, "Current data: ${mEventsLiveData.value.toString()}")
			}
		}
		return mEventsLiveData
	}

	/**
	 * Attaches snapshot listener to document reference, and executes provided callback whenever
	 * the snapshot listener is triggered ( due to change in data etc..)
	 *
	 */
	private fun addQuerySnapShotListener(
		doc: CollectionReference,
		callback: (snapshot: QuerySnapshot) -> Unit
	) {
		doc.addSnapshotListener { snapshot, e ->
			when {
				e != null -> {
					Log.w(TAG, "Listen failed.", e)
					return@addSnapshotListener
				}
				snapshot != null -> {
					callback(snapshot)
				}
				else -> {
					Log.w(TAG, "Current data: null")
				}
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
		val events = mFirestore.collection("Events").document(event.id!!)

		mFirestore.runBatch {
			// add event id to user's events field
			it.update(userEvents, FieldPath.of("events"), FieldValue.arrayUnion(event.id))
			// add Event to participant's collection inside Event
			it.set(eventParticipantsDoc, user)
			// increment participantCount of Event
			it.update(events, FieldPath.of("participantCount"), FieldValue.increment(1))
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

		val events = mFirestore.collection("Events").document(event.id!!)

		mFirestore.runBatch {
			// Delete User doc inside Participant collection of Event
			it.delete(eventParticipantsDoc)
			// Remove event id from events field of User
			it.update(userEvents, FieldPath.of("events"), FieldValue.arrayRemove(event.id))
			// decrement participantCount
			it.update(events, FieldPath.of("participantCount"), FieldValue.increment(-1))
		}.addOnCompleteListener {
			if (it.isSuccessful) {
				Log.d(TAG, "UnJoining Events Successful")
			} else {
				Log.d(TAG, "UnJoining Events Failed")
			}
		}
	}

	fun getUserEvent(): LiveData<List<Event>>? {
		// The MediatorLiveData is used to serialize data coming from two asynchronous sources
		// i.e mUserProfileLiveData and mEventsLiveData, to get the events participated by a user
		// we need both of them. Hence, Using MediatorLiveDat we can add them as sources and when
		// the data of either changes the final output would be changed too.

		if (mUserEventsLiveData != null) {
			return mUserEventsLiveData
		}

		// Initialize live data in case it hasn't been
		if (mUserProfileLiveData.value == null) {
			getUserProfile()
		}
		if (mEventsLiveData.value == null) {
			getEvents()
		}

		mUserEventsLiveData = MediatorLiveData()

		mUserEventsLiveData!!.addSource(mUserProfileLiveData) {
			mUserEventsLiveData!!.value = liveDataChanged(mUserProfileLiveData, mEventsLiveData)
		}
		mUserEventsLiveData!!.addSource(mEventsLiveData) {
			mUserEventsLiveData!!.value = liveDataChanged(mUserProfileLiveData, mEventsLiveData)
		}

		return mUserEventsLiveData
	}

	private fun liveDataChanged(
		userLiveData: MutableLiveData<User>,
		eventsLiveData: MutableLiveData<List<Event>>
	): List<Event>? {
		if (userLiveData.value != null && eventsLiveData.value != null) {
			userLiveData.value?.let { user ->
				val eventIds = user.events
				val events = eventsLiveData.value

				if (eventIds != null && events != null) {
					return getMatchingEventsById(eventIds, events)
				}
			}
		} else{
			Log.d(TAG, "userLiveData = ${userLiveData.value} , eventsLiveData = ${eventsLiveData.value}")
		}
		return null
	}


	private fun getMatchingEventsById(
		eventIds: List<String>,
		events: List<Event>
	): ArrayList<Event> {
		val list = arrayListOf<Event>()
		for (id in eventIds) {
			val event: Event? = events.find {
				it.id.equals(id)
			}
			if (event != null) {
				list.add(event)
			}
		}
		Log.d("ViewModel", "Event list = ${list.count()}")
		return list
	}

	private fun fetchGlobalNotifications() {
		if (!::mGlobalNotificationsLiveData.isInitialized) {
			mGlobalNotificationsLiveData = MutableLiveData()
			val notifReference = mFirestore.collection("Notifications")

			addQuerySnapShotListener(notifReference) {
				val notificationObjects = it.toObjects(Notification::class.java)
				Log.d(TAG, "Number of Notifications = ${notificationObjects.size}")
				mGlobalNotificationsLiveData.value = notificationObjects
			}
		}
	}

	fun getNotification(): LiveData<List<Notification>> {

		if (!::mNotificationsLiveData.isInitialized) {
			fetchGlobalNotifications()
			fetchUserNotifications()
			mNotificationsLiveData = MediatorLiveData()
			mNotificationsLiveData.addSource(mGlobalNotificationsLiveData) {
				mNotificationsLiveData.value = getAllNotifications()
			}
			mNotificationsLiveData.addSource(mUserNotificationsLiveData) {
				mNotificationsLiveData.value = getAllNotifications()
			}
		}

		return mNotificationsLiveData
	}

	private fun fetchUserNotifications() {
		if (!::mUserNotificationsLiveData.isInitialized) {
			mUserNotificationsLiveData = MutableLiveData()
			val currentUser = mFireAuth.currentUser
			if (currentUser != null) {
				val userNotificationColRef =
					mFirestore.collection("Users").document(currentUser.uid)
						.collection("Notifications")
				addQuerySnapShotListener(userNotificationColRef) {
					val notificationsList = it.toObjects(Notification::class.java)
					mUserNotificationsLiveData.value = notificationsList
				}
			}
		}
	}

	private fun getAllNotifications(): List<Notification>? {
		val notificationsList: MutableList<Notification> = mutableListOf()

		mUserNotificationsLiveData.value?.let {
			notificationsList.addAll(it)
		}

		mGlobalNotificationsLiveData.value?.let {
			notificationsList.addAll(it)
		}

		return notificationsList
	}


}

