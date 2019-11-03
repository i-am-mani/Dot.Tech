package com.omega.dottech2k20.Adapters

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_events.*
import kotlinx.android.synthetic.main.item_events.view.*

class EventItem(val event: Event) : Item() {


	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			val store = FirebaseStorage.getInstance()
			event.thumbNail?.let{
				val reference = store.getReference(it)
				Glide.with(itemView).load(reference).into(im_event_image)
			}
		}
	}


	override fun getLayout(): Int {
		return R.layout.item_events
	}

}