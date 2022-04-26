package com.exemple.testingfeatures.features.bigfiles.splash

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.exemple.testingfeatures.R
import com.exemple.testingfeatures.databinding.FragmentSplashBinding

class CleanSplash : Fragment(R.layout.fragment_splash) {

    private val viewBinding: FragmentSplashBinding by viewBinding(FragmentSplashBinding::bind)
    private var size: Float = 0F

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (arguments?.getFloat("cleanSize") as Float).let {
            size = it
        }
        viewBinding.tvTitle.text = "Удаление файлов"
        timer.start()
    }

    private val timer = object : CountDownTimer(3000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            findNavController().navigate(R.id.action_cleanSplash_to_bigFilesFragmentDone, bundleOf(
                "size" to size
            ))
        }

    }
}
