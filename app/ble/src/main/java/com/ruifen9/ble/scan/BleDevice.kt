package com.ruifen9.ble.scan

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanSettings
import androidx.core.util.isNotEmpty
import no.nordicsemi.android.support.v18.scanner.ScanRecord

class BleDevice(
    val bluetoothDevice: BluetoothDevice,
    val rssi: Int,
    private val scanRecord: ScanRecord?
) {

    val receiveTimeMillis = System.currentTimeMillis()

    val localName: String = scanRecord?.deviceName ?: ""

    val bytes = scanRecord?.bytes

    val manufacturerData = scanRecord?.manufacturerSpecificData?.run {
        if (isNotEmpty()) {
            this.valueAt(0)
        } else {
            null
        }
    }

    /**
     *  广播包 =》... FF ad cd [Manufacturer Specific Data]
     *
     * @param manufacturerId [0,65535]
     *
     * [ ad cd ] to Int ManufacturerId
     */
    fun getManufacturerSpecificData(manufacturerId: Int): ByteArray? {
        return scanRecord?.getManufacturerSpecificData(manufacturerId)
    }

}