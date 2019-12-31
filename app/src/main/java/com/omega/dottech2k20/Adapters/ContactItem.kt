package com.omega.dottech2k20.Adapters

import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewPropertyAnimator
import android.widget.TextView
import com.omega.dottech2k20.Models.Contact
import com.omega.dottech2k20.R
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
				setAnimation(viewHolder)
			}
		}

	}

	private fun setAnimation(viewHolder: GroupieViewHolder) {
		viewHolder.apply {
			card_contact.setOnClickListener {
				val state = tv_contact_post.visibility
				if (state == VISIBLE) {
//					card_contact.postDelayed({tv_contact_post.visibility = GONE},500)
					val post = setTextViewAnimation(tv_contact_post, 0, GONE)
					val name = setTextViewAnimation(tv_contact_name, 500, VISIBLE)
					val detail = setTextViewAnimation(tv_contact_detail, 500, VISIBLE)

					card_contact.animate().rotationYBy(180f).apply {
						duration = 1000
						withStartAction {
							post.start()
							name.start()
							detail.start()
						}
						start()
					}
				} else {
					val post = setTextViewAnimation(tv_contact_post, 0, VISIBLE, -1)
					val name = setTextViewAnimation(tv_contact_name, 500, GONE, -1)
					val detail = setTextViewAnimation(tv_contact_detail, 500, GONE, -1)

					card_contact.animate().rotationYBy(-180f).apply {
						duration = 1000
						withStartAction {
							post.start()
							name.start()
							detail.start()
						}
						start()
					}
				}
			}
		}
	}

	fun setTextViewAnimation(
		view: TextView,
		delay: Long,
		visibility: Int,
		direction: Int = 1
	): ViewPropertyAnimator {
		return view.animate().rotationYBy(180f * direction).apply {
			duration = 500
			startDelay = delay
			withStartAction {
				view.visibility = visibility
			}
		}
	}

	override fun getLayout(): Int {
		return R.layout.item_contact
	}

}