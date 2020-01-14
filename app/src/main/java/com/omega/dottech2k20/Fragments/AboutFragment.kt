package com.omega.dottech2k20.Fragments


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.omega.dottech2k20.MainActivity
import com.omega.dottech2k20.R
import com.omega.dottech2k20.models.MetaDataViewModel
import kotlinx.android.synthetic.main.fragment_about.*


class AboutFragment : Fragment() {

	lateinit var mActivity: MainActivity
	lateinit var mViewModel: MetaDataViewModel
	val TAG = javaClass.simpleName


	override fun onAttach(context: Context) {
		super.onAttach(context)
		try {
			mActivity = context as MainActivity
		} catch (e: Exception) {
			Log.d(TAG, "Error occured while casting")
		}
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		mViewModel = ViewModelProviders.of(mActivity).get(MetaDataViewModel::class.java)
		mViewModel.getAboutDescription().observe(this, Observer {
			if (it != null) {
				pb_about.visibility = View.GONE
				tv_about_description.visibility = View.VISIBLE
				tv_about_description.text = Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
			}
		})
	}


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_about, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setViewSourceCodeCallback()
		setViewAttributionCallback()
	}

	private fun setViewSourceCodeCallback() {
		btn_view_source_code.setOnClickListener {
			goToWebSite("https://github.com/llGOKUll/Dot.Tech")
		}
		card_facebook.setOnClickListener {
			goToWebSite("https://www.facebook.com/dotTech2015/")
		}
		card_instagram.setOnClickListener {
			goToWebSite("https://instagram.com/dottech_2020?igshid=1cn0mt06kmahp")
		}

	}

	private fun setViewAttributionCallback() {
		btn_view_attribution.setOnClickListener {
			startActivity(Intent(mActivity, OssLicensesMenuActivity::class.java))
		}
	}

	private fun goToWebSite(url: String) {
		val viewIntent = Intent(
			"android.intent.action.VIEW",
			Uri.parse(url)
		)
		startActivity(viewIntent)
	}


}
