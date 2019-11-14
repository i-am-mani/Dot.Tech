package com.omega.dottech2k20.Adapters

import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_image.*


class EventImageItem(val imageUrl: String) : Item() {

	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			imageUrl.let {
				if (it.contains(Regex("https|HTTPS"))) {
					Glide.with(itemView).load(it).into(im_event_image)
				} else {
					val reference = FirebaseStorage.getInstance().getReference(it)
					Glide.with(itemView).load(reference).into(im_event_image)
				}
			}
		}
	}

	override fun getLayout(): Int = R.layout.item_event_image

}