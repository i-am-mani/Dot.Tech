package com.omega.dottech2k20.Fragments


import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.omega.dottech2k20.Adapters.EventImageItem
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.HorizontalEqualSpaceItemDecoration
import com.omega.dottech2k20.Utils.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_event_details.*

class FragmentEventDetails : Fragment() {

	private val TAG: String = javaClass.simpleName
	lateinit var mEvent: Event
	val mAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Log.d(TAG, "Bundle values - ${arguments?.toString()}")
		mEvent = arguments?.getParcelable("event")!!
		return inflater.inflate(R.layout.fragment_event_details, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRV()
		initEventDetails()
	}

	private fun initEventDetails() {
		tv_event_title.text = mEvent.title
		tv_event_details.text = Html.fromHtml(mEvent.longDescription, Html.FROM_HTML_MODE_LEGACY)
	}

	private fun initRV() {
		generateItems()

		rv_event_images.adapter = mAdapter
		rv_event_images.layoutManager =
			LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
		context?.let {
			LinearSnapHelper().attachToRecyclerView(rv_event_images)
			rv_event_images.addItemDecoration(
				HorizontalEqualSpaceItemDecoration(
					Utils.convertDPtoPX(
						it,
						10
					)
				)
			)
		}

	}

	private fun generateItems() {
		val items = mutableListOf<EventImageItem>()

		mEvent.images?.let { images ->
			for (image in images) {
				Log.d(TAG, "Image - $image added")
				items.add(EventImageItem(image))
			}
		}

		mAdapter.addAll(items)
	}


}
