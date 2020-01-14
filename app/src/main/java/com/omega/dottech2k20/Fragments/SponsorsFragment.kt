package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.omega.dottech2k20.Adapters.SponsorItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.models.Sponsor
import com.omega.dottech2k20.models.SponsorsViewModel
import com.rd.animation.type.AnimationType
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_sponsors.*
import java.lang.Math.abs

class SponsorsFragment : Fragment() {

	private lateinit var mAdapter: GroupAdapter<GroupieViewHolder>
	private lateinit var mViewModel: SponsorsViewModel
	private val linearLayoutManager =
		LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

	lateinit var mActivity: MainActivity

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mAdapter = GroupAdapter()
		mViewModel = ViewModelProviders.of(mActivity).get(SponsorsViewModel::class.java)
		mViewModel.getSponsorsData().observe(this, Observer { sponsors ->
			if (sponsors != null && sponsors.count() > 0) {
				val sponsorItems = getSponsorItems(sponsors)
				if (mAdapter.itemCount > 0) {
					mAdapter.update(sponsorItems)
				} else {
					mAdapter.addAll(sponsorItems)
				}
				sponsor_layout_group.visibility = View.VISIBLE
				pb_sponsors.visibility = View.GONE
				indicator_sponsors.count = mAdapter.itemCount // specify total count of indicators
			}
		})
		return inflater.inflate(R.layout.fragment_sponsors, container, false)
	}

	private fun getSponsorItems(sponsors: List<Sponsor>): List<SponsorItem> {
		val items = mutableListOf<SponsorItem>()
		for (sponsor in sponsors) {
			context?.let { items.add(SponsorItem(it, sponsor)) }
		}
		return items
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		rv_sponsors.layoutManager =
			linearLayoutManager
		rv_sponsors.adapter = mAdapter
		rv_sponsors.isNestedScrollingEnabled = false
		LinearSnapHelper().attachToRecyclerView(rv_sponsors)

		indicator_sponsors.count = 1
		indicator_sponsors.selection = 0
		indicator_sponsors.setAnimationType(AnimationType.DROP)
		indicator_sponsors.radius = 10

		var position = 0
		rv_sponsors.addOnScrollListener(object : RecyclerView.OnScrollListener() {

			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				super.onScrollStateChanged(recyclerView, newState)
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					val curPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
					if (curPosition != position) {
						indicator_sponsors.selection = curPosition
						position = curPosition
					}
				}
			}
		})

		imbtn_next.setOnClickListener {
			val curPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
			position = abs(curPosition + 1) % mAdapter.itemCount
			linearLayoutManager.scrollToPosition(position)
			indicator_sponsors.selection = position
		}

		imbtn_previous.setOnClickListener {
			val curPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
			position = abs(curPosition - 1) % mAdapter.itemCount
			linearLayoutManager.scrollToPosition(position)
			indicator_sponsors.selection = position
		}
	}
}
