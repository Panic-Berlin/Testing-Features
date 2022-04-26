package com.exemple.testingfeatures.features.bigfiles

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.exemple.testingfeatures.R
import com.exemple.testingfeatures.databinding.FragmentFilesBinding
import java.io.File

class BigFilesFragment : Fragment(R.layout.fragment_files) {

    private val viewBinding: FragmentFilesBinding by viewBinding(FragmentFilesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val dir = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).path
        )
        Log.d(TAG, "abs: $dir")
        val f = File(dir.toString())
        val list = f.listFiles()?.filter {
            Log.d(TAG, "abs: ${it.length()}")
            it.length() / 1024 / 1024 > 50
        }
        if (list.isNullOrEmpty()){
            viewBinding.tvNull.isVisible = true
            viewBinding.rvFiles.isVisible = false
            viewBinding.btnClean.isVisible = false
        }else{
            viewBinding.tvNull.isVisible = false
            viewBinding.rvFiles.isVisible = true
            viewBinding.btnClean.isVisible = true
        }
        Log.d(TAG, "abs: $list")
        val adapter = list?.let { BigFilesAdapter(it) }
        viewBinding.rvFiles.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.rvFiles.adapter = adapter?.apply {
            this.load()
        }
        adapter?.selectedCount?.observe(viewLifecycleOwner){
            if (it <= 0) {
                with(viewBinding.btnClean) {
                    isEnabled = false
                }
            } else {
                with(viewBinding.btnClean) {
                    isEnabled = true
                }
            }
        }
        viewBinding.btnClean.setOnClickListener {
            adapter?.cleanSelected(requireContext())
            findNavController().navigate(R.id.action_bigFilesFragment_to_cleanSplash, bundleOf(
                "size" to adapter?.cleanedSize
            ))
        }
    }


}
