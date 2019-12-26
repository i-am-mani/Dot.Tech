package com.omega.dottech2k20.Models

import android.os.Parcel
import android.os.Parcelable

data class Notice(
	val id: String? = null,
	val priority: Int = 0,
	val title: String? = null,
	val image: String? = null,
	val content: String? = null
) : Parcelable {
	constructor(source: Parcel) : this(
		source.readString(),
		source.readInt(),
		source.readString(),
		source.readString(),
		source.readString()
	)

	override fun describeContents() = 0

	override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
		writeString(id)
		writeInt(priority)
		writeString(title)
		writeString(image)
		writeString(content)
	}

	companion object {
		@JvmField
		val CREATOR: Parcelable.Creator<Notice> = object : Parcelable.Creator<Notice> {
			override fun createFromParcel(source: Parcel): Notice = Notice(source)
			override fun newArray(size: Int): Array<Notice?> = arrayOfNulls(size)
		}
	}
}