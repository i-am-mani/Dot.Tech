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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omega.dottech2k20.Adapters.ContactItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.Models.Contact
import com.omega.dottech2k20.Models.MetaDataViewModel
import com.omega.dottech2k20.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_contacts.*

/**
 * A simple [Fragment] subclass.
 */
class ContactsFragment : Fragment() {


	lateinit var mActivity: MainActivity
	lateinit var mViewModel: MetaDataViewModel
	lateinit var mAdapter: GroupAdapter<GroupieViewHolder>

	val TAG = javaClass.simpleName

	override fun onAttach(context: Context) {
		super.onAttach(context)
		try {
			mActivity = context as MainActivity
		} catch (e: Exception) {
			Log.d(TAG, "Error occured while casting")
		}
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(MetaDataViewModel::class.java)
		mViewModel.getContacts().observe(this, Observer {
			if (it != null) {
				val contactItems: List<ContactItem> = getContactItems(it)
				if (mAdapter.itemCount == 0) {
					mAdapter.addAll(contactItems)
				} else {
					mAdapter.update(contactItems)
				}
			}
		})
	}

	private fun getContactItems(contacts: List<Contact>): List<ContactItem> {
		val contactsItem = mutableListOf<ContactItem>()
		for (contact in contacts) {
			contactsItem.add(ContactItem(contact))
		}
		return contactsItem
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mAdapter = GroupAdapter()
		return inflater.inflate(R.layout.fragment_contacts, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		rv_contacts.adapter = mAdapter
		rv_contacts.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
		rv_contacts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val view = recyclerView.getChildAt(0)
				if (view != null && recyclerView.getChildAdapterPosition(view) === 0) {
					val card = card_header_icon
					Log.d(TAG, "view.top = ${view.top}")
					// Basically we are moving against the scroll, applying same magnitude of scroll on opposite direction
					card.translationX = -((view.top) - 200) / 1f
				}
			}
		})
	}


}
