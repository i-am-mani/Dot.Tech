package com.omega.dottech2k20.Adapters

import com.omega.dottech2k20.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_total_count.*

class HeaderCountItem(val label: String = "Total", val count: Int) : Item() {
	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			tv_total_label.text = label
			tv_total_count.text = count.toString()
		}
	}

	override fun getLayout(): Int {
		return R.layout.item_total_count
	}

}