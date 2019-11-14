package com.omega.dottech2k20.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omega.dottech2k20.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentEventDetails : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_event_details, container, false)
	}

}
