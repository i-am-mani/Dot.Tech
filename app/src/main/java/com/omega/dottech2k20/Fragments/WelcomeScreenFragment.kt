package com.omega.dottech2k20.Fragments


import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.omega.dottech2k20.R
import com.omega.dottech2k20.StartUpActivity
import com.omega.dottech2k20.Utils.Utils
import kotlinx.android.synthetic.main.fragment_welcome_screen.*


class WelcomeScreenFragment : Fragment() {
	val TAG = javaClass.simpleName
	lateinit var mActivity: StartUpActivity
	val DURATION = 800L
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_welcome_screen, container, false)
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		mActivity = context as StartUpActivity
	}

	private fun addText() {
		val welcomeString = getString(R.string.splash_screen_welcome_string)
		val welcomeStringList: List<String> = welcomeString.split("/")

		for (i in welcomeStringList) {
			val tempTV = TextView(context)
			tempTV.apply {
				gravity = Gravity.CENTER
				text = i
				setTextAppearance(R.style.TitleTextRubik)
				setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
				alpha = 0f
			}
			root_welcome_text.addView(tempTV)
		}
	}

	private fun addButton() {
		context?.let {
			val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

			val px = Utils.convertDPtoPX(it, 28)
			params.setMargins(0, px, 0, 0)

			context?.let { ctx ->
				val button = MaterialButton(ctx)
			button.apply {
				layoutParams = params
				background.setColorFilter(
					getColor(context, R.color.MaterialGreen),
					PorterDuff.Mode.SRC_ATOP
				)
				setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
				text = "Go"
				alpha = 0f
			}
			button.setOnClickListener {
				mActivity.goToOnBoardingFragment()
			}
			root_welcome_text.addView(button)
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		addText()
		addButton()
		animateFirstTimeSplashScreen()
	}

	private fun animateFirstTimeSplashScreen() {

		// Introduce a slight delay
		Handler().postDelayed(Runnable {
			val calculateDelay = { i: Int -> i * DURATION + i * 100 }
			val children = root_welcome_text.children
			var i = 0

			for (child in children) {
				Log.d(TAG, "childText = ${(child as TextView).text}")
				child.animate().alpha(1f).apply {
					duration = DURATION
					startDelay = calculateDelay(i)
				}.start()
				i++
			}
		}, 300)


	}

}
