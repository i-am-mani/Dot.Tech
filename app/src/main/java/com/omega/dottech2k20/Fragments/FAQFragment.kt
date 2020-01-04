package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.omega.dottech2k20.Adapters.FAQItem
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.AuthenticationUtils
import com.omega.dottech2k20.dialogs.UserQueryDialog
import com.omega.dottech2k20.models.FAQ
import com.omega.dottech2k20.models.MetaDataViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_faq.*


class FAQFragment : Fragment() {

	lateinit var mActivity: MainActivity
	lateinit var mViewModel: MetaDataViewModel
	val mAdapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
	val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
	var currentUser: FirebaseUser? = null


	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as MainActivity
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(MetaDataViewModel::class.java)
		mViewModel.getFAQs().observe(this, Observer {
			if (it != null) {
				if (mAdapter.itemCount > 0) {
					mAdapter.update(getFAQItems(it))
				} else {
					mAdapter.addAll(getFAQItems(it))
					rv_faq.visibility = View.VISIBLE
					background_image.visibility = View.VISIBLE

					pb_faq.visibility = View.GONE
				}
			}
		})

		if (currentUser != null) {
			fab_request_new_faq.show()
		}
	}

	private fun getFAQItems(faqs: List<FAQ>): List<FAQItem> {
		val listOfItems = mutableListOf<FAQItem>()
		for (faq in faqs) {
			listOfItems.add(FAQItem(faq))
		}

		return listOfItems
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// logged in user
		currentUser = AuthenticationUtils.currentUser
		return inflater.inflate(R.layout.fragment_faq, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRecyclerView()
		setUpScrollListener()
		setUpFabClickListener()
	}

	private fun setUpFabClickListener() {
		fab_request_new_faq.setOnClickListener {
			currentUser?.let { user ->
				context?.let { ctx ->
					UserQueryDialog(ctx).apply {
						title = "Query Request"
						name = user.email ?: ""
						headerIconId = R.drawable.ic_faq
						onSubmit = { _, query ->
							mViewModel.requestQuery(user.uid, query)
						}
						build()
					}
				}
			}

		}
	}

	private fun setUpScrollListener() {
		rv_faq.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val view = recyclerView.getChildAt(0)
				if (view != null) {
					val card = view.findViewById<CardView>(R.id.card_faq)
					// Basically we are moving against the scroll, applying same magnitude of scroll on opposite direction
					card.translationX = -(view.top) / 2f
				}
			}
		})
	}

	private fun initRecyclerView() {
		rv_faq.layoutManager = layoutManager
		rv_faq.adapter = mAdapter

		rv_faq.addItemDecoration(
			DividerItemDecoration(
				context,
				layoutManager.orientation
			)
		)
	}


}
