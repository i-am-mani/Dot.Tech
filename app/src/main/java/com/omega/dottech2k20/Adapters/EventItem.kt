package com.omega.dottech2k20.Adapters

import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_events.*

class EventItem(val event: Event) : Item() {


	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			val store = FirebaseStorage.getInstance()
			event.thumbNail?.let{
				if (it.contains(Regex("https|HTTPS"))) {
					Glide.with(itemView).load(it).into(im_event_image)
				} else {
					val reference = store.getReference(it)
					Glide.with(itemView).load(reference).into(im_event_image)
				}
			}
		}
	}


	override fun getLayout(): Int {
		return R.layout.item_events
	}

}