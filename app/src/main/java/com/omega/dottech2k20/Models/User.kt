package com.omega.dottech2k20.Models


data class User(
	var id: String? = null,
	val fullName: String? = null,
	val email: String? = null,
	val phone: String? = null,
	val events: List<String>? = null,
	var notificationIds: List<String>? = null
)