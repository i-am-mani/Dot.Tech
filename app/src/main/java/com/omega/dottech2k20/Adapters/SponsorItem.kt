package com.omega.dottech2k20.Adapters

import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.Models.Sponsor
import com.omega.dottech2k20.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_sponsor.*

class SponsorItem(val sponsor: Sponsor) : Item() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			val (title, image, description, checkoutAddress) = sponsor
			if (title != null && image != null && description != null) {
				tv_sponsor_title.text = title
				tv_sponsor_content.text = description

				if (image.contains(Regex("https|HTTPS"))) {
					Glide.with(itemView).load(image).into(im_sponsor)
				} else {
					val reference = FirebaseStorage.getInstance().getReference(image)
					Glide.with(itemView).load(reference).into(im_sponsor)
				}
			}
		}
	}

	override fun getLayout(): Int {
		return R.layout.item_sponsor
	}

}