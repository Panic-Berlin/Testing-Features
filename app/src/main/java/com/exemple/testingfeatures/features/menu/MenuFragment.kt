package com.exemple.testingfeatures.features.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.exemple.testingfeatures.R
import com.exemple.testingfeatures.databinding.FragmentMenuBinding

class MenuFragment: Fragment(R.layout.fragment_menu) {

    private val viewBinding: FragmentMenuBinding by viewBinding(FragmentMenuBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        viewBinding.btnFiles.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_bigFilesSplashFragment)
        }
    }
}
