package com.omega.dottech2k20.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class Event(
    @Exclude var id: String? = null,
    var title: String? = null,
    var thumbNail: String? = null,
    var shortDescription: String? = null,
    var participantCount: Int? = null,
    @ServerTimestamp val startTime: Timestamp? = null,
    @ServerTimestamp val endTime: Timestamp? = null
)

