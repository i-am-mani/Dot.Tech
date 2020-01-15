package com.omega.dottech2k20.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.Utils
import com.omega.dottech2k20.models.Notification
import kotlinx.android.synthetic.main.fragment_notification_detail.*

class NotificationDetailFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_notification_detail, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val notification = extractNotification()
		setImage(notification.image)
		setTitle(notification.title)
		setDate(notification.issued_time)
		setContent(notification.content)
		setOnScrollListener()
	}

	private fun setOnScrollListener() {
//		root_notification_detail.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//			im_notification.translationY = -(im_notification.top / 2f)
//		}
	}

	private fun setContent(content: String?) {
		content?.let {
			tv_notification_content.text = content
		}
	}

	private fun setDate(issuedTime: Timestamp?) {
		if (issuedTime != null) {
			tv_notification_date.text = Utils.getFormattedTime(issuedTime)
		} else {
			tv_notification_date.visibility = GONE
		}
	}

	private fun setTitle(title: String?) {
		title?.let {
			tv_notification_title.text = title
		}
	}

	private fun setImage(image: String?) {
		val ctx = context
		if (image != null && ctx != null && image.isNotEmpty()) {
			if (image.contains(Regex("https|HTTPS"))) {
				Glide.with(ctx).load(image).placeholder(Utils.getCircularDrawable(ctx))
					.into(im_notification)
			} else {
				val reference = FirebaseStorage.getInstance().getReference(image)
				Glide.with(ctx).load(reference).placeholder(Utils.getCircularDrawable(ctx))
					.into(im_notification)
			}
		} else {
			card_im_notification.visibility = View.GONE
		}
	}

	private fun extractNotification(): Notification {
		val data = arguments?.get(NOTIFICATION_BUNDLE_KEY) as Notification
		return data
	}

	companion object {
		val NOTIFICATION_BUNDLE_KEY = "Notification"
	}

}
