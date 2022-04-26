package com.exemple.testingfeatures.features.bigfiles.splash

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.exemple.testingfeatures.R

class BigFilesSplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer.start()
    }

    private val timer = object : CountDownTimer(3000, 1000){
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            findNavController().navigate(R.id.action_bigFilesSplashFragment_to_bigFilesFragment)
        }

    }
}
