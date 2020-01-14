package com.omega.dottech2k20.Adapters

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.Utils
import com.omega.dottech2k20.models.Notice
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_start_up_notice.*

class NoticeItem(val context: Context, val notice: Notice) : Item() {

	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			notice.title?.let {
				tv_notice_title.text = it
			}
			notice.content?.let {
				tv_notice_content.text = it
			}
			val image: String? = notice.image
			if (image != null) {
				if (image.contains(Regex("https|HTTPS"))) {
					Glide.with(itemView).load(image).placeholder(Utils.getCircularDrawable(context))
						.into(im_notice)
				} else {
					val reference = FirebaseStorage.getInstance().getReference(image)
					Glide.with(itemView).load(reference)
						.placeholder(Utils.getCircularDrawable(context)).into(im_notice)
				}
			} else {
				im_notice.visibility = View.GONE
			}

		}
	}

	override fun getLayout(): Int {
		return R.layout.item_start_up_notice
	}

}