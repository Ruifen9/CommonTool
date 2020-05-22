package com.ruifen9.commontool.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData


class CheckEnvironmentUtils(private val context: Context) {

    var currentEnv = Environment(bluetooth = false, location = false, locationPermission = false)

    fun getEnvironmentLiveData(): MutableLiveData<Environment> {
        return environmentLiveData
    }

    fun checkEnv(): Environment {
        val ble = hasBluetoothOn()
        val locationEnable = hasLocationOn()
        val permission = hasAllowLocationPermission()
        currentEnv = Environment(ble, locationEnable, permission)
        return currentEnv
    }

    fun registerChanged() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, intentFilter)
    }

    fun unregisterChanged() {
        context.unregisterReceiver(receiver)
    }

    ///////////////////////////////////////// Private /////////////////////////////////////////////////////////////

    private val environmentLiveData = MutableLiveData<Environment>()
    private val bleAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action ?: return) {
                LocationManager.PROVIDERS_CHANGED_ACTION,
                BluetoothAdapter.ACTION_STATE_CHANGED -> check()
            }
        }
    }

    init {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(receiver, intentFilter)

        check()
    }


    private fun check() {
        val ble = hasBluetoothOn()
        val locationEnable = hasLocationOn()
        val permission = hasAllowLocationPermission()
        currentEnv = Environment(ble, locationEnable, permission)
        environmentLiveData.postValue(currentEnv)
    }

    private fun hasBluetoothOn(): Boolean {
        return bleAdapter.isEnabled
    }

    private fun hasLocationOn(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager?.isLocationEnabled ?: false
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

}