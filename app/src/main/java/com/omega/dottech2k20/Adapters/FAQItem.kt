package com.omega.dottech2k20.Adapters

import android.view.View
import com.omega.dottech2k20.R
import com.omega.dottech2k20.models.FAQ
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_faq.*

class FAQItem(val FAQ: FAQ) : Item() {

	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		viewHolder.apply {
			val question = FAQ.question
			val answer = FAQ.answer
			if (question.isNotEmpty() && answer.isNotEmpty()) {
				tv_faq_question.text = question
				tv_faq_answer.text = answer

				card_faq.setOnClickListener {
					tv_faq_answer.visibility = if (tv_faq_answer.visibility == View.GONE) {
						View.VISIBLE
					} else {
						View.GONE
					}
					updateImage(viewHolder)
				}
			}
		}
	}

	private fun updateImage(viewHolder: GroupieViewHolder) {
		viewHolder.apply {
			val visibility = tv_faq_answer.visibility
			if (visibility == View.GONE) {
				imbtn_toggle_content.setImageResource(R.drawable.ic_arrow_down)
			} else {
				imbtn_toggle_content.setImageResource(R.drawable.ic_arrow_up)
			}
		}
	}

	override fun getLayout(): Int {
		return R.layout.item_faq
	}

}