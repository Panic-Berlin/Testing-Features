package com.exemple.testingfeatures.features.menu

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.exemple.testingfeatures.R
import com.exemple.testingfeatures.databinding.FragmentMenuBinding
import com.exemple.testingfeatures.databinding.ItemPopupBinding


class MenuFragment : Fragment(R.layout.fragment_menu) {

    private val viewBinding: FragmentMenuBinding by viewBinding(FragmentMenuBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        viewBinding.btnFiles.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_bigFilesSplashFragment)
        }
        viewBinding.btnCpu.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_coolDownFragment)
        }
        viewBinding.btnPopup.setOnClickListener {
            val inflater =
                requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val pw = PopupWindow(
                inflater.inflate(R.layout.item_popup, null, false),
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                true
            )
            pw.showAtLocation(requireActivity().findViewById(R.id.main), Gravity.CENTER, 0, 0)
            val view = pw.contentView
            val later = view.findViewById<AppCompatButton>(R.id.btn_later)
            val ok = view.findViewById<AppCompatButton>(R.id.btn_ok)
            later.setOnClickListener {
                pw.dismiss()
            }
            ok.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data =
                    Uri.parse("https://play.google.com/store/apps/details?id=ru.tinkoff.invest.course")
                startActivity(i)
            }
        }
    }
}
