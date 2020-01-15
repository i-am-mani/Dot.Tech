package com.omega.dottech2k20.Adapters

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.omega.dottech2k20.R
import com.omega.dottech2k20.Utils.HorizontalEqualSpaceItemDecoration
import com.rd.animation.type.AnimationType
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_horizontal_image_viewer.*
import kotlin.math.abs

class HorizontalImageViewerItem(val context: Context, val imageUrls: List<String>?) : Item() {

	val adapter: GroupAdapter<GroupieViewHolder> = GroupAdapter()
	lateinit var layoutManger: LinearLayoutManager
	var mPosition = 0

	override fun bind(viewHolder: GroupieViewHolder, position: Int) {
		layoutManger = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
		viewHolder.apply {
			if (imageUrls != null && imageUrls.isNotEmpty()) {
				val imageItems = getImageItems()
				adapter.addAll(imageItems)
				if (rv_event_images.layoutManager == null) {
					rv_event_images.layoutManager = layoutManger
				}
				rv_event_images.adapter = adapter
				// Indicator dots
				initIndicator(imageItems.count(), viewHolder)

				rv_event_images.addItemDecoration(HorizontalEqualSpaceItemDecoration(10))
				LinearSnapHelper().attachToRecyclerView(rv_event_images)
			} else {
				root_event_images.visibility = View.GONE
			}

			btn_next.setOnClickListener {
				val pos = layoutManger.findFirstVisibleItemPosition()
				mPosition = abs(pos + 1) % layoutManger.itemCount
				rv_event_images.smoothScrollToPosition(mPosition)
				indicator_horizontal_viewer.selection = mPosition
			}

			btn_prev.setOnClickListener {
				val pos = layoutManger.findFirstVisibleItemPosition()
				mPosition = abs(pos - 1) % layoutManger.itemCount
				rv_event_images.smoothScrollToPosition(mPosition)
				indicator_horizontal_viewer.selection = mPosition
			}

		}
	}

	private fun initIndicator(
		count: Int,
		viewHolder: GroupieViewHolder
	) {
		viewHolder.apply {
			indicator_horizontal_viewer.count = count
			indicator_horizontal_viewer.selection = 0
			indicator_horizontal_viewer.setAnimationType(AnimationType.DROP)
			indicator_horizontal_viewer.radius = 8

			rv_event_images.addOnScrollListener(object : RecyclerView.OnScrollListener() {
				override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
					super.onScrollStateChanged(recyclerView, newState)
					if (newState == RecyclerView.SCROLL_STATE_IDLE) {
						var curPosition = layoutManger.findFirstVisibleItemPosition()
						if (curPosition != mPosition) {
							indicator_horizontal_viewer.selection = curPosition
							mPosition = curPosition
						}
					}
				}
			})
		}
	}


	fun getImageItems(): MutableList<EventImageItem> {
		val listOfItems = mutableListOf<EventImageItem>()
		if (imageUrls != null) {
			for (img in imageUrls) {
				listOfItems.add(EventImageItem(img))
			}
		}
		return listOfItems
	}

	override fun getLayout(): Int {
		return R.layout.item_horizontal_image_viewer
	}

}