package com.ruifen9.ble.env

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruifen9.ble.R

class BleEnvFragment : Fragment() {

    var envCheck: CheckEnvironmentUtils? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ble_env, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        envCheck = CheckEnvironmentUtils(view.context)
        envCheck?.registerChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        envCheck
    }



}