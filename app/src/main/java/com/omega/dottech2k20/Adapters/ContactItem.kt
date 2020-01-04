package com.omega.dottech2k20.Adapters

import com.omega.dottech2k20.R
import com.omega.dottech2k20.models.Contact
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_contact.*

class ContactItem(val contact: Contact) : Item() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		val (post, name, contactDetail) = contact
		if (post != null && name != null && contactDetail != null) {
			viewHolder.apply {
				tv_contact_post.text = post
				tv_contact_name.text = name
				tv_contact_detail.text = contactDetail
			}
		}

	}

	override fun getLayout(): Int {
		return R.layout.item_contact
	}

}