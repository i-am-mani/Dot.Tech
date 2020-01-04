package com.omega.dottech2k20.models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

	private lateinit var mGlobalNotificationsLiveData: MutableLiveData<List<Notification>>
	private lateinit var mUserNotificationsLiveData: MutableLiveData<List<Notification>>
	private lateinit var mNotificationsLiveData: MediatorLiveData<List<Notification>>

	private val mFirestore = FirebaseFirestore.getInstance()
	private val mFireAuth = FirebaseAuth.getInstance()
	private val TAG: String = javaClass.simpleName

	private fun initGlobalNotifications() {
		if (!::mGlobalNotificationsLiveData.isInitialized) {
			mGlobalNotificationsLiveData = MutableLiveData()
			val notifReference = mFirestore.collection("Notifications")

			FirebaseSnapshotListeners.addQuerySnapShotListener(notifReference) {
				val notificationObjects = it.toObjects(Notification::class.java)
				Log.d(TAG, "Number of Notifications = ${notificationObjects.size}")
				mGlobalNotificationsLiveData.value = notificationObjects
			}
		}
	}

	fun getNotification(): LiveData<List<Notification>> {

		if (!::mNotificationsLiveData.isInitialized) {
			initGlobalNotifications()
			initUserNotifications()
			mNotificationsLiveData = MediatorLiveData()
			mNotificationsLiveData.addSource(mGlobalNotificationsLiveData) {
				mNotificationsLiveData.value = mergeAllNotifications()
			}
			mNotificationsLiveData.addSource(mUserNotificationsLiveData) {
				mNotificationsLiveData.value = mergeAllNotifications()
			}
		}

		return mNotificationsLiveData
	}

	private fun initUserNotifications() {
		// FIXME: When user logs in after notification live data's are initialized then the user level notification won't work, until restart
		if (!::mUserNotificationsLiveData.isInitialized) {
			mUserNotificationsLiveData = MutableLiveData()
			val currentUser = mFireAuth.currentUser
			if (currentUser != null) {
				val userNotificationColRef =
					mFirestore.collection("Users").document(currentUser.uid)
						.collection("Notifications")
				FirebaseSnapshotListeners.addQuerySnapShotListener(userNotificationColRef) {
					val notificationsList = it.toObjects(Notification::class.java)
					mUserNotificationsLiveData.value = notificationsList
				}
			}
		}
	}

	private fun mergeAllNotifications(): List<Notification>? {
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