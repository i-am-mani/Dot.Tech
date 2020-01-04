package com.omega.dottech2k20.models

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

object FirebaseSnapshotListeners {

	private val TAG: String = javaClass.simpleName

	/**
	 * Listeners to Changes in Collection
	 *
	 * Attaches snapshot listener to document reference, and executes provided callback whenever
	 * the snapshot listener is triggered ( due to change in data etc..)
	 *
	 */
	fun addQuerySnapShotListener(
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
	 * Listens to Changes in Document
	 *
	 * Attaches snapshot listener to document reference, and executes provided callback whenever
	 * the snapshot listener is triggered (i.e due to change in data)
	 */
	fun addDocumentSnapShotListener(
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


}