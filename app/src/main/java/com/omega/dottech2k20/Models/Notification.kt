package com.omega.dottech2k20.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Notification(
	val title: String? = null,
	val content: String? = null,
	@ServerTimestamp val issued_time: Timestamp? = null,
	val image: String? = null
)