package com.omega.dottech2k20


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.transition.addListener
import kotlinx.android.synthetic.main.fragment_splash_screen.*
import kotlinx.android.synthetic.main.fragment_splash_screen2.*

class SplashScreenFragment : Fragment() {

    lateinit var mainActivity: StartUpActivity
    var isFirstTime: Boolean = false


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as StartUpActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref =
            context?.getSharedPreferences(Utils.sharedPreferenceName, Context.MODE_PRIVATE)!!
        isFirstTime = sharedPref.getBoolean("first-time", true)

        return if (isFirstTime) {
            sharedPref.edit().putBoolean("first-time", false).apply()
            inflater.inflate(R.layout.fragment_splash_screen, container, false)
        } else {
            return inflater.inflate(R.layout.fragment_splash_screen2, container, false)
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFirstTime) {
            animateFirstTimeSplashScreen()
        } else {
            // If Delay isn't added, then transition won't be visible.
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
    }

    private fun animateFirstTimeSplashScreen() {
        val calculateDelay = { i: Int -> i * DURATION + i * 100 }

        text_welcome.animate().alpha(1f).apply { duration = DURATION }
        text_to.animate().alpha(1f).apply {
            duration = DURATION
            startDelay = calculateDelay(2) - 100
        }
        text_dot_tech.animate().alpha(1f).apply {
            duration = DURATION
            startDelay = calculateDelay(3)
        }
        text_2020.animate().alpha(1f).apply {
            duration = DURATION
            startDelay = calculateDelay(4)
        }.withEndAction {
            Handler(Looper.getMainLooper()).postDelayed(
                { mainActivity.goToOnBoardingFragment() },
                500
            )
        }
    }

    companion object {

        const val DURATION: Long = 500

        @JvmStatic
        fun newInstance() =
            SplashScreenFragment()
    }
}
