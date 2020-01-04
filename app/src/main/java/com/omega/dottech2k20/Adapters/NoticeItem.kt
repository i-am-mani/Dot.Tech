package com.omega.dottech2k20.Adapters

import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.R
import com.omega.dottech2k20.models.Notice
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_start_up_notice.*

class NoticeItem(val notice: Notice) : Item() {

	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			notice.title?.let {
				tv_notice_title.text = it
			}
			notice.content?.let {
				tv_notice_content.text = it
			}
			notice.image?.let {
				if (it.contains(Regex("https|HTTPS"))) {
					Glide.with(itemView).load(it).into(im_notice)
				} else {
					val reference = FirebaseStorage.getInstance().getReference(it)
					Glide.with(itemView).load(reference).into(im_notice)
				}
			}

		}
	}

	override fun getLayout(): Int {
		return R.layout.item_start_up_notice
	}

}