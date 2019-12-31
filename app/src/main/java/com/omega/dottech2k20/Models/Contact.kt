package com.omega.dottech2k20.Models

/**
 * Model for Contact Details.
 *
 * Post is the designation of the person
 * name is the person's full name
 * contactDetail refer to any form of contact detail, like email, phone etc
 */
data class Contact(
	val post: String? = null,
	val name: String? = null,
	val contactDetail: String? = null
)