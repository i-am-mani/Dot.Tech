package com.omega.dottech2k20



import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_splash_screen.*

class SplashScreenFragment : Fragment() {

    lateinit var mainActivity:StartUpActivity;


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = context as StartUpActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mainView = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calculateDelay = { i:Int  -> i * DURATION + i*100 }

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
            duration = DURATION + 300
            startDelay = calculateDelay(4)
        }.withEndAction{mainActivity.goToOnBoardingFragment()}
    }

    companion object {

        val DURATION: Long = 500

        @JvmStatic
        fun newInstance() =
            SplashScreenFragment()
    }
}
