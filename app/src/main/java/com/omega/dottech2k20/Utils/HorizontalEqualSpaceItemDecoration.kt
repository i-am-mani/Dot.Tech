package com.omega.dottech2k20.Utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State

class HorizontalEqualSpaceItemDecoration(private val left: Int, private val right: Int) :
	ItemDecoration() {

	override fun getItemOffsets(
		outRect: Rect,
		view: View,
		parent: RecyclerView,
		state: State
	) {
		outRect.left = left
		outRect.right = right
	}

}