package com.omega.dottech2k20.Adapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.adapter_events.view.*

class EventsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imEvent: ImageView = itemView.im_event_image
    var store = FirebaseStorage.getInstance()

    fun onBind(imSource: String){
        Log.d("FIRESTORE",imSource)
        val reference = store.getReference(imSource)
        Glide.with(itemView).load(reference).into(imEvent)
    }
}