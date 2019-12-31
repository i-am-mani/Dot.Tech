package com.omega.dottech2k20


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.omega.dottech2k20.Adapters.FAQItem
import com.omega.dottech2k20.Models.FAQ
import com.omega.dottech2k20.Models.MetaDataViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.item_report_header.*


class ReportFragment : Fragment() {

	lateinit var mActivity: MainActivity
	lateinit var mViewModel: MetaDataViewModel
	lateinit var mAdapter: GroupAdapter<GroupieViewHolder>
	var currentUser: FirebaseUser? = null
	val mAdapterSection: Section = Section()

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(MetaDataViewModel::class.java)
		mViewModel.getReports().observe(this, Observer {
			if (it != null) {
				// As a temporary measure using FAQ item since both have same purpose to
				// show description upon clicking it. Fix this later.
				val listOfReports = mutableListOf<FAQItem>()

				for (report in it) {
					listOfReports.add(FAQItem(FAQ(report.title ?: "", report.description ?: "")))
				}

				if (mAdapterSection.itemCount > 0) {
					mAdapterSection.addAll(listOfReports)
				} else {
					mAdapterSection.update(listOfReports)
				}
			}
		})
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		mAdapter = GroupAdapter()
		mAdapter.add(mAdapterSection)
		mAdapterSection.setHeader(ReportHeaderItem())
		return inflater.inflate(R.layout.fragment_report, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

		rv_reports.adapter = mAdapter
		rv_reports.layoutManager = layoutManager
	}

	inner class ReportHeaderItem : Item() {
		override fun bind(
			viewHolder: com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder,
			position: Int
		) {
			viewHolder.apply {
				fab_report_bug.setOnClickListener {

				}
				fab_request.setOnClickListener {

				}
			}
		}

		override fun getLayout(): Int {
			return R.layout.item_report_header
		}

	}


}
