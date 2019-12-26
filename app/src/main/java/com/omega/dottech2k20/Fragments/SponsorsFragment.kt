package com.omega.dottech2k20.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.omega.dottech2k20.Adapters.SponsorItem
import com.omega.dottech2k20.Models.Sponsor
import com.omega.dottech2k20.Models.SponsorsViewModel
import com.omega.dottech2k20.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_sponsors.*

class SponsorsFragment : Fragment() {

	val mAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
	lateinit var mViewModel: SponsorsViewModel

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mViewModel = ViewModelProviders.of(this).get(SponsorsViewModel::class.java)
		mViewModel.getSponsorsData().observe(this, Observer { sponsors ->
			if (sponsors != null) {
				val sponsorItems = getSponsorItems(sponsors)
				if (mAdapter.itemCount > 0) {
					mAdapter.update(sponsorItems)
				} else {
					mAdapter.addAll(sponsorItems)
				}
			}
		})
		return inflater.inflate(R.layout.fragment_sponsors, container, false)
	}

	private fun getSponsorItems(sponsors: List<Sponsor>): List<SponsorItem> {
		val items = mutableListOf<SponsorItem>()
		for (sponsor in sponsors) {
			items.add(SponsorItem(sponsor))
		}
		return items
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		rv_sponsors.layoutManager =
			LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
		rv_sponsors.adapter = mAdapter
		rv_sponsors.isNestedScrollingEnabled = false
	}
}
