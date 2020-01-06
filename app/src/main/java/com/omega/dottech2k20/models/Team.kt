package com.omega.dottech2k20.models

data class Team(
	val id: String? = null,
	val name: String? = null,
	val creator: String? = null, // id of the creator of team
	val passcode: String? = null,
	val teammates: List<Teammate> = listOf()
)