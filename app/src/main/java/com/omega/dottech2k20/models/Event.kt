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
	val visibleParticipants: List<Participant> = listOf(),
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
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		TODO("visibleParticipants"),
		parcel.readInt(),
		parcel.readString(),
		parcel.createStringArrayList(),
		parcel.readParcelable(Timestamp::class.java.classLoader),
		parcel.readParcelable(Timestamp::class.java.classLoader),
		parcel.readByte() != 0.toByte(),
		parcel.readInt(),
		parcel.readString(),
		parcel.readValue(Int::class.java.classLoader) as? Int
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(id)
		parcel.writeString(title)
		parcel.writeString(thumbnail)
		parcel.writeString(shortDescription)
		parcel.writeInt(participantCount)
		parcel.writeString(longDescription)
		parcel.writeStringList(images)
		parcel.writeParcelable(startTime, flags)
		parcel.writeParcelable(endTime, flags)
		parcel.writeByte(if (registrationOpen) 1 else 0)
		parcel.writeInt(orderPreference)
		parcel.writeString(type)
		parcel.writeValue(teamSize)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Event> {
		override fun createFromParcel(parcel: Parcel): Event {
			return Event(parcel)
		}

		override fun newArray(size: Int): Array<Event?> {
			return arrayOfNulls(size)
		}
	}
}