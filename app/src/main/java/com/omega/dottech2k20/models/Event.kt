package com.omega.dottech2k20.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.omega.dottech2k20.Utils.FirestoreFieldNames

data class Event(
	val id: String? = null,
	val title: String? = null,
	val thumbnail: String? = null,
	val shortDescription: String = "",
	val visibleParticipants: HashMap<String, String> = HashMap(),
	val participantCount: Int = 0,
	val longDescription: String = "",
	val images: List<String> = listOf(),
	@ServerTimestamp val startTime: Timestamp? = null,
	@ServerTimestamp val endTime: Timestamp? = null,
	val registrationOpen: Boolean = true,
	val orderPreference: Int = 100000, // if not specified give least priority (considering ascending order listing)
	val type: String = FirestoreFieldNames.EVENT_TYPE_INDIVIDUAL,
	val teamSize: Int? = null
) : Parcelable {
	constructor(source: Parcel) : this(
		source.readString(),
		source.readString(),
		source.readString(),
		source.readString(),
		source.readSerializable() as HashMap<String, String>,
		source.readInt(),
		source.readString(),
		source.createStringArrayList(),
		source.readParcelable<Timestamp>(Timestamp::class.java.classLoader),
		source.readParcelable<Timestamp>(Timestamp::class.java.classLoader),
		1 == source.readInt(),
		source.readInt(),
		source.readString(),
		source.readValue(Int::class.java.classLoader) as Int?
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeString(id)
		writeString(title)
		writeString(thumbnail)
		writeString(shortDescription)
		writeSerializable(visibleParticipants)
		writeInt(participantCount)
		writeString(longDescription)
		writeStringList(images)
		writeParcelable(startTime, 0)
		writeParcelable(endTime, 0)
		writeInt((if (registrationOpen) 1 else 0))
		writeInt(orderPreference)
		writeString(type)
		writeValue(teamSize)
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Event> = object : Parcelable.Creator<Event> {
			override fun createFromParcel(source: Parcel): Event = Event(source)
			override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
		}
	}
}