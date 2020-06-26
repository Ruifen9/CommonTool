package com.ruifen9.ble.scan

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanSettings
import no.nordicsemi.android.support.v18.scanner.ScanRecord

class BleDevice(
    val bluetoothDevice: BluetoothDevice,
    val rssi: Int,
    private val scanRecord: ScanRecord?
) {


    val localName: String = scanRecord?.deviceName ?: ""

    val bytes = scanRecord?.bytes

    /**
     *  广播包 =》... FF ad cd [Manufacturer Specific Data]
     *
     * @param manufacturerId [0,65535]
     *
     * [ ad cd ] to Int ManufacturerId
     */
    fun getManufacturerSpecificData(manufacturerId: Int): ByteArray? {
        ScanSettings.SCAN_MODE_BALANCED
        return scanRecord?.getManufacturerSpecificData(manufacturerId)
    }

}