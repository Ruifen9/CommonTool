package com.ruifen9.ble.env

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent


//请使用懒加载，
class CheckEnvironmentUtils(private val context: Context) : LifecycleObserver {

    private val environmentLiveData = MutableLiveData<Environment>()

    private val bleAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action ?: return) {
                LocationManager.PROVIDERS_CHANGED_ACTION,
                BluetoothAdapter.ACTION_STATE_CHANGED -> checkEnv()
            }
        }
    }

    init {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, intentFilter)

        checkEnv()
    }

    data class Environment(
        var bluetooth: Boolean,
        var location: Boolean,
        var locationPermission: Boolean
    ) {
        fun ready(): Boolean {
            return bluetooth && location && locationPermission
        }

        override fun toString(): String {
            return "Environment(bluetooth=$bluetooth, location=$location, locationPermission=$locationPermission)"
        }
    }


    /**
     * 只有在运行环境变化时才会post
     */
    fun getEnvironmentLiveData(): MutableLiveData<Environment> {
        return environmentLiveData
    }

    /**
     * 主动检查运行环境
     */
    fun checkEnv(): Environment {
        val ble = hasBluetoothOn()
        val locationEnable = hasLocationOn()
        val permission = hasAllowLocationPermission()
        currentEnv = Environment(
            ble,
            locationEnable,
            permission
        )
        environmentLiveData.postValue(currentEnv)
        return currentEnv
    }

    /**
     * 未来会变动
     *
     * 如何使用：
     * step1:
     *  val utils= CheckEnvironmentUtils(context)
     * step2:
     *  utils.link(activity)
     */
    fun link(activity: FragmentActivity) {

        getEnvironmentLiveData().observe(activity, Observer {
            val fm = activity.supportFragmentManager
            if (it.ready()) {
                val envFragment = fm.findFragmentByTag("env")
                if (envFragment != null) {
                    fm.beginTransaction()
                        .remove(envFragment)
                        .commitNowAllowingStateLoss()
                }
            } else {
                val parent = activity.window.decorView
                if (parent.id == View.NO_ID) {
                    parent.id = View.generateViewId()
                }
                fm.beginTransaction()
                    .replace(parent.id, BleEnvFragment(), "env")
                    .commitNowAllowingStateLoss()
            }
        })
        activity.lifecycle.addObserver(this)
    }


    ///////////////////////////////////////// Private /////////////////////////////////////////////////////////////

    private var currentEnv =
        Environment(
            bluetooth = false,
            location = false,
            locationPermission = false
        )

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreated() {
        Log.d("疑问", "activity 已经onCreated,再初始化该类，还会进入该方法吗？")
        registerChanged()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        unregisterChanged()
    }

    private fun registerChanged() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, intentFilter)
    }

    private fun unregisterChanged() {
        context.unregisterReceiver(receiver)
    }

//    private fun check() {
//        val ble = hasBluetoothOn()
//        val locationEnable = hasLocationOn()
//        val permission = hasAllowLocationPermission()
//        currentEnv = Environment(
//            ble,
//            locationEnable,
//            permission
//        )
//        environmentLiveData.postValue(currentEnv)
//    }

    private fun hasBluetoothOn(): Boolean {
        return bleAdapter.isEnabled
    }

    private fun hasLocationOn(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager?.isLocationEnabled ?: false
        } else {
            val locationMode = try {
                Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE
                )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
                return false
            }
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        }
    }


    private fun hasAllowLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PERMISSION_GRANTED
    }

}