package com.omega.dottech2k20.models

import android.app.Application
import android.util.Log
import android.widget.Toast
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
	private lateinit var mNoticeLiveData: MutableLiveData<List<Notice>>
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
	 * 	Update User data in Users Collection.
	 *
	 * 	Note :- the data is being replaced!
	 */
	fun updateUserProfile(user: User) {

		user.id?.let { id ->
			mFirestore.collection("Users").document(id).set(user).addOnCompleteListener {
				if (it.isSuccessful) {
					Log.d(TAG, "Updating user Successful!")
				}
			}
		}
	}

	/**
	 *  Updates given user's information across all it's instances e.g Event's Participant's
	 *  sub collection.
	 *
	 *  Note: The Data would be replaced and not merged.
	 */
	fun updateUserInformation(user: User, onSuccessCallback: () -> Unit) {
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

						// Update name in visibleParticipants Map of Event
						user.fullName?.let { name ->
							val eventDoc = mFirestore.collection("Events")
								.document(eventIds)
							batch.update(
								eventDoc,
								FieldPath.of("visibleParticipants"),
								hashMapOf(userId to name)
							)
						}

						batch.set(eventParticipantDoc, user)
					}
				}
			}
		}.addOnCompleteListener {
			if (it.isSuccessful) {
				onSuccessCallback()
			}
		}
	}


	/**
	 * Listens to Changes in Document
	 *
	 * Attaches snapshot listener to document reference, and executes provided callback whenever
	 * the snapshot listener is triggered (i.e due to change in data)
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

	/**
	 * Return LiveData Instance of List of All Events.
	 *
	 * Note:- Only one snapshot listener is attached irrespective of number of calls. Once the
	 * SnapShot listener is attached, the same is returned when requested, instead of attaching new
	 * listener.
	 *
	 */
	fun getEvents(): LiveData<List<Event>> {
		val eventTask = mFirestore.collection("Events").orderBy(FieldPath.of("orderPreference"))

		if (mEventsLiveData.value == null) {
			addQuerySnapShotListener(eventTask) {
				mEventsLiveData.value = it.toObjects(Event::class.java)
				Log.d(TAG, "Current data: ${mEventsLiveData.value.toString()}")
			}
		}
		return mEventsLiveData
	}

	/**
	 * Listenes to Changes in Collection
	 *
	 * Attaches snapshot listener to document reference, and executes provided callback whenever
	 * the snapshot listener is triggered ( due to change in data etc..)
	 *
	 */
	private fun addQuerySnapShotListener(
		doc: Query,
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


	/**
	 * Adds currently signed in user to the Event.
	 *
	 * Executes queries in Batch, writes to Participants sub-collection of Event as well as Adds the
	 * Event Id to User's profile, and increment participantCount of Event.
	 *
	 * @param event : Event which user will join
	 */
	fun joinEvent(event: Event, isAnonymous: Boolean) {
		var user: User? = mUserProfileLiveData.value
		if (user == null) {
			val uid = mFireAuth.currentUser?.uid ?: return

			val document: DocumentReference = mFirestore.collection("Users").document(uid)
			document.get().addOnCompleteListener { task ->
				if (task.isSuccessful) {
					// Check of nullability
					task.result?.let { documentSnapshot ->
						documentSnapshot.toObject(User::class.java)?.let {
							mUserProfileLiveData.value = it
							addUserInEvent(event, it, isAnonymous)
						}
					}

					Toast.makeText(
						getApplication(),
						"Joining Event Successfull",
						Toast.LENGTH_SHORT
					).show()

				} else {
					Log.e(TAG, "Failed To get User : ", task.exception)
				}
			}
		} else {
			addUserInEvent(event, user, isAnonymous)
		}
	}

	private fun addUserInEvent(
		event: Event,
		user: User,
		isAnonymous: Boolean = false // By Default allow listing
	) {
		// Inside Sub-collection ( Participants ), set the id of participant document as UID
		// done to easily retrieve the document
		val eventId = event.id
		val userId = user.id
		if (eventId != null && userId != null && user.fullName != null) {
			val eventParticipantsDoc = mFirestore
				.collection("Events")
				.document(eventId).collection("Participants").document(userId)
			val userEvents = mFirestore.collection("Users").document(userId)
			val events = mFirestore.collection("Events").document(eventId)

			// visibleParticipants - with user permission make the user's name public in listing
			// those who do not wish their names to be made public, will be shown as anonymous users
			// i.e the difference between visibleParticipants count and participants could
			val visibleParticipants = event.visibleParticipants
			if (!isAnonymous) {
				visibleParticipants[userId] = user.fullName
			}

			mFirestore.runBatch {
				// add event id to user's events field
				it.update(userEvents, FieldPath.of("events"), FieldValue.arrayUnion(eventId))
				// add Event to participant's collection inside Event
				it.set(eventParticipantsDoc, user)
				// increment participantCount of Event
				it.update(events, FieldPath.of("participantCount"), FieldValue.increment(1))
				// update event's visibleParticipants
				it.update(events, FieldPath.of("visibleParticipants"), visibleParticipants)
			}.addOnCompleteListener {
				if (it.isSuccessful) {
					Log.d(TAG, "Joining Events Successful")
				} else {
					Log.d(TAG, "Joining Events Failed")
				}
			}
		} else {
			Log.w(TAG, "event or user id is null")
		}
	}

	/**
	 * UnJoin or Leave the Event which the currently signed in user is part of(joined).
	 *
	 * The user instances from this events Participants sub-collection is deleted, ParticipantCount
	 * is decremented and from User collection the Event id is removed from `events` array.
	 */
	fun unjoinEvents(event: Event) {

		var user: User? = mUserProfileLiveData.value

		if (user == null) {
			val uid = mFireAuth.currentUser?.uid ?: return

			val document: DocumentReference = mFirestore.collection("Users").document(uid)
			document.get().addOnCompleteListener {
				if (it.isSuccessful) {
					it.result?.let { res ->
						val userProfile = res.toObject(User::class.java)
						if (userProfile != null) {
							mUserProfileLiveData.value = userProfile
							removeUserFromEvent(event, userProfile)
						}
					}
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

		val eventId = event.id
		val userId = user.id
		if (eventId != null && userId != null && user.fullName != null) {
			val eventParticipantsDoc = mFirestore
				.collection("Events")
				.document(eventId).collection("Participants").document(userId)

			val userEvents = mFirestore.collection("Users").document(userId)

			val events = mFirestore.collection("Events").document(event.id)

			val visibleParticipants = event.visibleParticipants
			visibleParticipants.remove(userId)

			mFirestore.runBatch {
				// Delete User doc inside Participant collection of Event
				it.delete(eventParticipantsDoc)
				// Remove event id from events field of User
				it.update(userEvents, FieldPath.of("events"), FieldValue.arrayRemove(event.id))
				// decrement participantCount
				it.update(events, FieldPath.of("participantCount"), FieldValue.increment(-1))
				// remove participant from visibleParticipant map
				it.update(events, FieldPath.of("visibleParticipants"), visibleParticipants)
			}.addOnCompleteListener {
				if (it.isSuccessful) {
					Log.d(TAG, "UnJoining Events Successful")
				} else {
					Log.d(TAG, "UnJoining Events Failed")
				}
			}
		}
	}

	/**
	 *  Returns LiveData of List of Events which the User has joined.
	 *
	 *  This computed from User's `events` field, which is a list of event ids, and Events.
	 *  The events from Events data having id in common with `events` field of user are added to list.
	 */
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

		mUserEventsLiveData?.let {
			it.addSource(mUserProfileLiveData) {
				mUserEventsLiveData?.value = liveDataChanged(mUserProfileLiveData, mEventsLiveData)
			}
			it.addSource(mEventsLiveData) {
				mUserEventsLiveData?.value = liveDataChanged(mUserProfileLiveData, mEventsLiveData)
			}
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
		} else {
			Log.d(
				TAG,
				"userLiveData = ${userLiveData.value} , eventsLiveData = ${eventsLiveData.value}"
			)
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

	private fun getNotices(): LiveData<List<Notice>> {
		if (!::mNoticeLiveData.isInitialized) {
			mNoticeLiveData = MutableLiveData()
			val query = mFirestore.collection("Notices")
			query.get().addOnCompleteListener {
				if (it.isSuccessful) {
					val result = it.result
					if (result != null) {
						mNoticeLiveData.value = result.toObjects(Notice::class.java)
					} else {
						Log.e(TAG, "Failed to fetch notices: ", it.exception)
					}
				}
			}
		}
		return mNoticeLiveData
	}
}

