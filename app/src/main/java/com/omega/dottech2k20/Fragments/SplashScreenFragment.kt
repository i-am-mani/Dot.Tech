package com.omega.dottech2k20.Fragments


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.transition.addListener
import androidx.fragment.app.Fragment
import com.omega.dottech2k20.R
import com.omega.dottech2k20.StartUpActivity
import kotlinx.android.synthetic.main.fragment_splash_screen.*

class SplashScreenFragment : Fragment() {

	lateinit var mainActivity: StartUpActivity


	override fun onAttach(context: Context) {
		super.onAttach(context)
		mainActivity = context as StartUpActivity
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		return inflater.inflate(R.layout.fragment_splash_screen, container, false)
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setAnimator()
	}

	private fun setAnimator() {
		Handler(Looper.getMainLooper()).postDelayed({
			var constraintSet = ConstraintSet()
			constraintSet.clone(splash_screen_constraint_set_root)

			// Animate Name and Logo in center
			constraintSet.connect(
				R.id.im_event_logo,
				ConstraintSet.BOTTOM,
				R.id.guideline_mid,
				ConstraintSet.TOP
			)
			constraintSet.connect(
				R.id.text_fest_name,
				ConstraintSet.TOP,
				R.id.guideline_mid,
				ConstraintSet.BOTTOM
			)

			val changeBounds = ChangeBounds().apply {
				interpolator = AccelerateDecelerateInterpolator()
				duration = 1000
			}

			// Go to Main Activity
			changeBounds.addListener(onEnd = {
				mainActivity.goToMainActivity()
			})

			constraintSet.applyTo(splash_screen_constraint_set_root)

			TransitionManager.beginDelayedTransition(
				splash_screen_constraint_set_root,
				changeBounds
			)
		}, 300)
	}


	companion object {

		const val DURATION: Long = 500

		@JvmStatic
		fun newInstance() =
			SplashScreenFragment()
	}
}
