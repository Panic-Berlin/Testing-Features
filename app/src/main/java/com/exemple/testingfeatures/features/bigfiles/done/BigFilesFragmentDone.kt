package com.exemple.testingfeatures.features.bigfiles.done

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.exemple.testingfeatures.R
import com.exemple.testingfeatures.databinding.FragmentDoneBinding

class BigFilesFragmentDone: Fragment(R.layout.fragment_done) {

    private val viewBinding: FragmentDoneBinding by viewBinding(FragmentDoneBinding::bind)
    private var size: Float = 0F

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        (arguments?.getFloat("cleanSize") as Float).let {
            size = it
        }

        viewBinding.tvTitle.text = convertSizeToString(size)
    }

    private fun convertSizeToString(size: Float): String {
        val spc = arrayOf("B", "KB", "MB", "GB", "TB")
        var c = 0
        var prev = size
        while (prev / 1024 > 1 && c < 5) {
            prev /= 1024
            c++
        }
        return String.format("%.1f", prev) + " " + spc[c]
    }
}
