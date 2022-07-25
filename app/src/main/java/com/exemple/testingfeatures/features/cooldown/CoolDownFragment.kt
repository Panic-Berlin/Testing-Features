package com.exemple.testingfeatures.features.cooldown

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.exemple.testingfeatures.R
import com.exemple.testingfeatures.databinding.FragmentCpuBinding
import java.io.BufferedReader
import java.io.FileReader
import java.lang.Exception


class CoolDownFragment: Fragment(R.layout.fragment_cpu) {

    private val viewBinding: FragmentCpuBinding by viewBinding(FragmentCpuBinding::bind)


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.tvCpu.isVisible = false
        try {
            viewBinding.tvCpu.isVisible = true
            val bufferedReader = BufferedReader(FileReader( "/sys/class/thermal/thermal_zone0/temp")).readLine()
            val cpu = bufferedReader.toInt() / 1000
            viewBinding.tvCpu.text = "CPU: $cpu°C"
            Log.d("Temp", "onViewCreated: $cpu")
            val batteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            requireContext().registerReceiver(batteryReceiver, batteryIntentFilter)
        }catch (e: Exception){
            Log.d("Temp", "onViewCreated: $e")
        }
    }

    private val batteryReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent) {
            val batteryTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toFloat() / 10
            viewBinding.tvBattery.text = "Battery: $batteryTemp°C"
            Log.d("Temp", "onReceive: Battery $batteryTemp°C")
        }
    }

}
