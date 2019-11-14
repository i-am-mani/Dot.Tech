package com.omega.dottech2k20.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Event(
	val id: String? = null,
	val title: String? = null,
	val thumbnail: String? = null,
	val shortDescription: String? = null,
	val participantCount: Int? = null,
	val longDescription: String? = null,
	val images: String? = null,
	@ServerTimestamp val startTime: Timestamp? = null,
	@ServerTimestamp val endTime: Timestamp? = null
)

