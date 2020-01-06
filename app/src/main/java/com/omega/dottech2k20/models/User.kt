package com.omega.dottech2k20.models


data class User(
	var id: String? = null,
	val fullName: String? = null,
	val email: String? = null,
	val phone: String? = null,
	val events: List<Ids> = listOf(),
	var notificationIds: List<String> = listOf()
)