package com.omega.dottech2k20.Adapters

import com.omega.dottech2k20.Models.FAQ
import com.omega.dottech2k20.R
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
			}
		}
	}

	override fun getLayout(): Int {
		return R.layout.item_faq
	}

}