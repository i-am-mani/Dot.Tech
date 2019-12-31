package com.omega.dottech2k20.Models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Notification(
	val title: String? = null,
	val content: String? = null,
	@ServerTimestamp val issued_time: Timestamp? = null,
	val image: String? = null
) : Parcelable {
	constructor(source: Parcel) : this(
		source.readString(),
		source.readString(),
		source.readParcelable<Timestamp>(Timestamp::class.java.classLoader),
		source.readString()
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeString(title)
		writeString(content)
		writeParcelable(issued_time, 0)
		writeString(image)
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Notification> = object : Parcelable.Creator<Notification> {
			override fun createFromParcel(source: Parcel): Notification = Notification(source)
			override fun newArray(size: Int): Array<Notification?> = arrayOfNulls(size)
		}
	}
}