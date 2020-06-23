package com.ruifen9.commontool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ruifen9.uicomponents.DimView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dimView.listener=object :DimView.OnProgressChangedListener{
            override fun onChanged(progress: Float) {

            }

            override fun progressTextMap(progress: Float): String {
                return "${progress*100}"
            }
        }

    }
}
