package com.omega.dottech2k20.Models

import com.google.firebase.firestore.ServerTimestamp

data class Notification(
	val title: String? = null,
	val content: String? = null,
	val issued_time: ServerTimestamp? = null,
	val image: String? = null
)