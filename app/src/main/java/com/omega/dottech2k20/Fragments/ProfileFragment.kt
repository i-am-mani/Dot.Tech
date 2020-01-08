package com.omega.dottech2k20.Fragments


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnFlingListener
import com.omega.dottech2k20.Adapters.UserEventItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.Utils.EventCallbacks
import com.omega.dottech2k20.Utils.FirestoreFieldNames
import com.omega.dottech2k20.Utils.Utils
import com.omega.dottech2k20.dialogs.BinaryDialog
import com.omega.dottech2k20.models.Event
import com.omega.dottech2k20.models.User
import com.omega.dottech2k20.models.UserEventViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_total_count.*

class ProfileFragment : Fragment() {
	val TAG = javaClass.simpleName
	lateinit var mViewModel: UserEventViewModel
	var mAdapter = GroupAdapter<GroupieViewHolder>()
	lateinit var mActivity: MainActivity
	lateinit var eventsGroupieSection: Section

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity

	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(UserEventViewModel::class.java)
		val profile = mViewModel.getUserProfile()

		val currentUser = AuthenticationUtils.currentUser
		if (currentUser != null) {

			profile?.observe(this, Observer {
				if (it != null) {
					setProfileDetails(it)
				}
			})
			mViewModel.getUserEvent()?.observe(this, Observer {
				if (it != null) {
					updateUserEvents(it)
				}
			})
		}
	}

	private fun updateUserEvents(events: List<Event>) {
		setLayoutManager()
		setAdapter(events)
	}

	private fun setLayoutManager() {
		rv_user_events.layoutManager =
			LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
	}

	private fun setAdapter(events: List<Event>) {
		val eventItems = getEventItems(events)

		if (!::eventsGroupieSection.isInitialized) {
			eventsGroupieSection = Section()
			eventsGroupieSection.setHeader(EventSummaryItem(eventItems.count()))
			eventsGroupieSection.addAll(eventItems)
			// Attach the section with Groupie adapter
			mAdapter.add(eventsGroupieSection)
		} else {
			// In case the event count has changed
			eventsGroupieSection.removeHeader()
			eventsGroupieSection.setHeader(EventSummaryItem(eventItems.count()))
			eventsGroupieSection.update(eventItems)
		}


		rv_user_events.adapter = mAdapter
	}


	private fun getEventItems(events: List<Event>): List<UserEventItem> {
		val list = arrayListOf<UserEventItem>()
		for (event in events) {
			list.add(UserEventItem(event, this::leaveEvent, this::viewDetails))
		}
		return list
	}

	private fun leaveEvent(event: Event) {
		// Since profile is visible would imply that user has logged in
		// and since unverified accounts cannot join event, both the checks aren't needed.
		context?.let { ctx ->
			when {
				event.type == FirestoreFieldNames.EVENT_TYPE_INDIVIDUAL -> {
					EventCallbacks.leave(ctx, event, mViewModel)
				}
				event.type == FirestoreFieldNames.EVENT_TYPE_TEAM -> {
					findNavController().navigate(
						R.id.eventTeamsFragment,
						bundleOf(
							EventTeamsFragment.EVENT_KEY to event,
							EventTeamsFragment.READ_ONLY to false
						)
					)
				}
			}
		}
	}

	private fun viewDetails(events: Event) {
		findNavController().navigate(
			R.id.event_details,
			bundleOf(EventDetailsFragment.EVENT_KEY to events)
		)
	}

	private fun setProfileDetails(user: User) {
		tv_full_name.text = user.fullName
		tv_email.text = user.email
		tv_phone.text = user.phone
		btn_edit_profile?.setOnClickListener {
			showEditDialog(user)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (AuthenticationUtils.currentUser == null) {
			return showUnsignedUserDialog(inflater, container)
		}
		return inflater.inflate(R.layout.fragment_profile, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setUpFlingListener()
		setUpScrollListener()
	}

	private fun setUpScrollListener() {
		rv_user_events?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val view = recyclerView.getChildAt(0)
				if (view != null && recyclerView.getChildAdapterPosition(view) === 0) {
					val card = view.findViewById<CardView>(R.id.card_total_count)
					Log.d(TAG, "view.top = ${view.top}")
					// Basically we are moving against the scroll, applying same magnitude of scroll on opposite direction
					card.translationY = -(view.top) / 2f
				}
			}
		})
	}

	private fun setUpFlingListener() {
		rv_user_events?.onFlingListener = (object :
			OnFlingListener() {
			private fun updateLayout(height: Int) {

				val layoutParams = root_profile_data.layoutParams
				context?.let {
					Utils.convertDPtoPX(it, height).let {
						layoutParams.height = it
					}
				}
				root_profile_data.layoutParams = layoutParams
				TransitionManager.beginDelayedTransition(root_profile, ChangeBounds())
			}

			override fun onFling(velocityX: Int, velocityY: Int): Boolean {
				// +ve Y = Swipe Up, -ve Y = Swipe Down
				if (velocityY > 1000) {
					Log.d(TAG, "Fling values = $velocityX, $velocityY")
					updateLayout(150)
					return true
				} else if (velocityY < -2000) {
					Log.d(TAG, "Fling values = $velocityX, $velocityY")
					updateLayout(250)
					return true
				}
				return false
			}
		})
	}


	fun showEditDialog(user: User) {
		val dialog = Dialog(context)
		setDialogProperties(dialog)

		val nameField = dialog.findViewById<EditText>(R.id.et_edit_fullname)
		val phoneField = dialog.findViewById<EditText>(R.id.et_edit_phone)
		val confirmBtn = dialog.findViewById<Button>(R.id.btn_confirm)

		confirmBtn.setOnClickListener {
			val name = nameField.text.toString()
			val phone = phoneField.text.toString()

			val isFullNameValid = Utils.isFullNameValid(name)
			val isPhoneNumberValid = Utils.isPhoneNumberValid(phone)

			when {
				// First provide meaningful error message i.e check if they're not empty and not valid.
				name.isNotEmpty() && !isFullNameValid -> showToast("Please check the name you have provided")

				phone.isNotEmpty() && !isPhoneNumberValid -> showToast("Invalid phone number format!")

				// whatever remains is valid, hence depending on field which is empty or not update user

				name.isNotEmpty() && phone.isNotEmpty() -> { // Update both name and phone number
					updateUser(user, name, phone)
				}

				name.isEmpty() && phone.isNotEmpty() -> { // Update phone number only
					user.fullName?.let { updateUser(user, it, phone) }
				}

				phone.isEmpty() && name.isNotEmpty() -> { // Update full name only
					user.phone?.let { updateUser(user, name, it) }
				}

				else -> showToast("Invalid Data Provided")
			}
			dialog.dismiss()
		}
		dialog.show()
	}

	private fun updateUser(
		user: User,
		name: String,
		phone: String
	) {
		val updatedUser =
			User(user.id, name, user.email, phone, user.events, user.notificationIds)
		mViewModel.updateUserInformation(updatedUser) {
			Toast.makeText(
				mActivity,
				"Updating User Details Successfull!",
				Toast.LENGTH_SHORT
			).show()
		}
	}

	private fun setDialogProperties(dialog: Dialog) {
		dialog.setCanceledOnTouchOutside(true)

		dialog.requestWindowFeature(Window.FEATURE_SWIPE_TO_DISMISS)
		dialog.setContentView(R.layout.dialog_edit_profile)
		dialog.window.setLayout(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT
		)
		dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
	}


	private fun showUnsignedUserDialog(
		inflater: LayoutInflater,
		container: ViewGroup?
	): View? {
		context?.let {
			BinaryDialog(it).apply {
				title = "Unsigned user"
				description = "Please Sign in or Sign Up to Continue"
				leftButtonName = "Sign In"
				rightButtonName = "Sign Up"
				leftButtonCallback = { findNavController().navigate(R.id.signInFragment) }
				rightButtonCallback = { findNavController().navigate(R.id.signUpFragment) }
			}.build()
		}
		return inflater.inflate(R.layout.unsigned_user_layout, container, false)
	}

	inner class EventSummaryItem(val count: Int) : Item() {
		override fun bind(viewHolder: GroupieViewHolder, position: Int) {
			viewHolder.apply {
				tv_total_count.text = count.toString()
			}
		}

		override fun getLayout(): Int {
			return R.layout.item_total_count
		}

	}

	fun showToast(message: String) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
	}


}
