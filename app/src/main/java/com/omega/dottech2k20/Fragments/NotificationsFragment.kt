package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.omega.dottech2k20.Adapters.NotificationItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.models.Notification
import com.omega.dottech2k20.models.NotificationsViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : Fragment() {

	private val TAG = javaClass.simpleName
	private lateinit var mActivity: MainActivity
	private lateinit var mAdapter: GroupAdapter<GroupieViewHolder>
	private lateinit var mViewModel: NotificationsViewModel

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mAdapter = GroupAdapter()
		return inflater.inflate(R.layout.fragment_notifications, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRV()
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity
	}


	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(NotificationsViewModel::class.java)
		mViewModel.getNotification().observe(this, Observer {
			it?.let { notificationList ->
				if (mAdapter.itemCount == 0) {
					mAdapter.addAll(getNotificationItem(it))
				} else {
					mAdapter.update(getNotificationItem(notificationList))
				}
				pb_notifications.visibility = GONE
			}
		})

	}

	private fun getNotificationItem(notificationList: List<Notification>): List<NotificationItem> {
		val list = arrayListOf<NotificationItem>()

		val sortedList = notificationList.sortedByDescending { it.issued_time?.toDate()?.time }
		for (notification in sortedList) {
			context?.let {
				val notificationItem =
					NotificationItem(it, findNavController(), notification)
				list.add(notificationItem)
			}
		}
		return list
	}

	private fun initRV() {
		rv_notifications.setHasFixedSize(true)
		rv_notifications.itemAnimator = null // To avoid Blinking of RV
		rv_notifications.adapter = mAdapter
		rv_notifications.layoutManager =
			LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
	}
}
