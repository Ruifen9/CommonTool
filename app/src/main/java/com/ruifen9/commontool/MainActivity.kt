package com.ruifen9.commontool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.ruifen9.ble.connect.BleConnection
import com.ruifen9.ble.connect.BleMgr
import com.ruifen9.ble.env.CheckEnvironmentUtils
import com.ruifen9.uicomponents.DimView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dimView.listener = object : DimView.OnProgressChangedListener {
            override fun onChanged(progress: Float) {

            }

            override fun progressTextMap(progress: Float): String {
                return "${progress * 100000}"
            }
        }


        dimView.min = 0.01f

        val env=CheckEnvironmentUtils(this)
        env.link(this)
        lifecycle.addObserver(env)

//        val connection = BleMgr.getInstance(application).createConnection()
//
//        lifecycleScope.launch(Dispatchers.Main) {
//            val result = connection.connect("FF:FF:BA:99:6C:B9")
//            Log.e("result", "$result")
//        }

    }
}
