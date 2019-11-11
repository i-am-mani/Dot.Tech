package com.omega.dottech2k20.Adapters

import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.Models.Notification
import com.omega.dottech2k20.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_notification.*


class NotificationItem(private val notification: Notification) : Item() {
	val TAG = javaClass.simpleName
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		if (notification.image != null) {
			setImage(viewHolder)
		}
		setTitleAndContent(viewHolder)
		setDateTime(viewHolder)
	}

	private fun setDateTime(viewHolder: GroupieViewHolder) {

	}

	private fun setTitleAndContent(viewHolder: GroupieViewHolder) {
		viewHolder.apply {
			Log.d(TAG, "Title = ${notification.title}")
			// Set Title
			tv_notification_title.text = notification.title
			// Set content
			tv_notification_content.text = notification.content
		}
	}

	private fun setImage(
		viewHolder: GroupieViewHolder
	) {
		val store = FirebaseStorage.getInstance()
		val image = notification.image
		viewHolder.apply {
			//Set Image
			image?.trim()?.let {
				Log.d(TAG, "image = $it")
				if (it.contains(Regex("https|HTTPS"))) {
					Glide.with(itemView).load(it).into(im_notification)
				} else {
					val reference = store.getReference(it)
					Glide.with(itemView).load(reference).into(im_notification)
				}
				root_im_notification.visibility = View.VISIBLE
			}
		}
	}

	override fun getLayout(): Int {
		return R.layout.item_notification
	}


}