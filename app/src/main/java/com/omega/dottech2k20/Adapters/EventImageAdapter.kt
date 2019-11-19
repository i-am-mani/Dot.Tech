package com.omega.dottech2k20.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.omega.dottech2k20.Models.Event
import com.omega.dottech2k20.R.id
import com.omega.dottech2k20.R.layout

class EventImageAdapter(private val mContext: Context) :
	Adapter<EventImageAdapter.ViewHolder>() {
	private var mDataset: List<Event>? = null
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ViewHolder {
		val itemView: View? = LayoutInflater.from(mContext)
			.inflate(layout.item_events, parent, false)
		return ViewHolder(itemView!!)
	}

	override fun onBindViewHolder(
		holder: ViewHolder,
		position: Int
	) {
		holder.onBind(mDataset!![position])
	}

	override fun getItemCount(): Int {
		return if (mDataset == null) {
			0
		} else {
			mDataset!!.size
		}
	}


	fun setDataSet(list: List<Event>?) {
		mDataset = list
		notifyDataSetChanged()
	}

	fun getItem(position: Int): Event? {
		return mDataset?.get(position)
	}

	inner class ViewHolder(itemView: View) :
		RecyclerView.ViewHolder(itemView) {
		private var imEvent: ImageView = itemView.findViewById(id.im_event_image)

		fun onBind(event: Event) {
			val imageUrl = event.thumbnail
			imageUrl?.let {
				Log.d("ImageViewItem", "Binding image - $imageUrl")
				if (it.contains(Regex("https|HTTPS"))) {
					Glide.with(itemView).load(it).into(imEvent)
				} else {
					val reference = FirebaseStorage.getInstance().getReference(it)
					Glide.with(itemView).load(reference).into(imEvent)
				}
			}
		}

	}

}