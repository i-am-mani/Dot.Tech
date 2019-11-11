package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.omega.dottech2k20.Adapters.NotificationItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.Models.Notification
import com.omega.dottech2k20.Models.UserEventViewModel
import com.omega.dottech2k20.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_notifications.*

/**
 * A simple [Fragment] subclass.
 */
class NotificationsFragment : Fragment() {

	private val TAG = javaClass.simpleName
	private lateinit var mActivity: MainActivity
	private var mAdapter: GroupAdapter<GroupieViewHolder>? = null
	private lateinit var mViewModel: UserEventViewModel

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_notifications, container, false)
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity
	}


	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(UserEventViewModel::class.java)
		mViewModel.getNotification().observe(this, Observer {
			it?.let { notificationList ->
				if (mAdapter == null) {
					initRV(notificationList)
				} else {
					mAdapter?.apply {
						update(getNotificationItem(notificationList))
					}
				}
			}
		})

	}

	private fun getNotificationItem(notificationList: List<Notification>): List<NotificationItem> {
		val list = arrayListOf<NotificationItem>()

		val sortedList = notificationList.sortedByDescending { it.issued_time?.toDate()?.time }
		for (notification in sortedList) {
			val notificationItem = NotificationItem(notification)
			Log.d(TAG, "Timestamp = ${notification.issued_time?.toDate()?.time}}")
			list.add(notificationItem)
		}
		return list
	}

	private fun initRV(notificationList: List<Notification>) {
		mAdapter = GroupAdapter()

		rv_notifications.setHasFixedSize(true)
		rv_notifications.itemAnimator = null // To avoid Blinking of RV
		rv_notifications.adapter = mAdapter
		rv_notifications.layoutManager =
			LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
		mAdapter?.addAll(getNotificationItem(notificationList))
	}

	override fun onDestroy() {
		super.onDestroy()
		mAdapter = null
	}


}
