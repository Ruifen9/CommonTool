package com.ruifen9.ble.env

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class EnvLiveData(private val context: Context) : LiveData<Environment>() {

    /**
     * 主动检查运行环境
     */
    fun checkEnv(): Environment {
        val ble = hasBluetoothOn()
        val locationEnable = hasLocationOn()
        val permission = hasAllowLocationPermission()
        val currentEnv = Environment(
            ble,
            locationEnable,
            permission
        )
        postValue(currentEnv)
        return currentEnv
    }

    fun autoShowEnvTips(activity: FragmentActivity){
        observe(activity, Observer {
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
    }

    private val bleAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action ?: return) {
                LocationManager.PROVIDERS_CHANGED_ACTION,
                BluetoothAdapter.ACTION_STATE_CHANGED -> checkEnv()
            }
        }
    }

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
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
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

   override fun onActive() {
        super.onActive()
        registerChanged()
        checkEnv()
    }

    override fun onInactive() {
        super.onInactive()
        unregisterChanged()
    }
}