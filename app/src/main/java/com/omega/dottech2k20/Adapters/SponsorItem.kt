package com.omega.dottech2k20.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.Utils
import com.omega.dottech2k20.models.Sponsor
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_sponsor.*

class SponsorItem(val context: Context, val sponsor: Sponsor) : Item() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			val (title, image, description, checkoutAddress) = sponsor
			// Title
			if (title != null) {
				tv_sponsor_title.text = title
				tv_sponsor_content.text = description
			}
			// Image
			if (image != null) {
				if (image.contains(Regex("https|HTTPS"))) {
					Glide.with(itemView).load(image).placeholder(Utils.getCircularDrawable(context))
						.into(im_sponsor)
				} else {
					val reference = FirebaseStorage.getInstance().getReference(image)
					Glide.with(itemView).load(reference).into(im_sponsor)
				}
			} else {
				im_sponsor.visibility = View.GONE
			}
			// Checkout Address
			if (checkoutAddress != null) {
				btn_sponsor_checkout.setOnClickListener {
					val viewIntent = Intent(
						"android.intent.action.VIEW",
						Uri.parse("http://www.github.com/")
					)
					context.startActivity(viewIntent)
				}
			} else {
				btn_sponsor_checkout.visibility = View.GONE
			}

		}
	}

	override fun getLayout(): Int {
		return R.layout.item_sponsor
	}

}