package com.omega.dottech2k20.Models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Event(
	val id: String? = null,
	val title: String? = null,
	val thumbnail: String? = null,
	val shortDescription: String? = null,
	val participantCount: Int? = null,
	val longDescription: String? = null,
	val images: List<String>? = null,
	@ServerTimestamp val startTime: Timestamp? = null,
	@ServerTimestamp val endTime: Timestamp? = null,
	@field:JvmField val isParticipationOpen: Boolean = true
) : Parcelable {
	constructor(source: Parcel) : this(
		source.readString(),
		source.readString(),
		source.readString(),
		source.readString(),
		source.readValue(Int::class.java.classLoader) as Int?,
		source.readString(),
		source.createStringArrayList(),
		source.readParcelable<Timestamp>(Timestamp::class.java.classLoader),
		source.readParcelable<Timestamp>(Timestamp::class.java.classLoader),
		1 == source.readInt()
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeString(id)
		writeString(title)
		writeString(thumbnail)
		writeString(shortDescription)
		writeValue(participantCount)
		writeString(longDescription)
		writeStringList(images)
		writeParcelable(startTime, 0)
		writeParcelable(endTime, 0)
		writeInt((if (isParticipationOpen) 1 else 0))
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Event> = object : Parcelable.Creator<Event> {
			override fun createFromParcel(source: Parcel): Event = Event(source)
			override fun newArray(size: Int): Array<Event?> = arrayOfNulls(size)
		}
	}
}