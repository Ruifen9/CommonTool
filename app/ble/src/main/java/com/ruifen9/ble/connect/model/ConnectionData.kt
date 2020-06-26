package com.ruifen9.ble.connect.model

import android.bluetooth.le.ScanRecord
import androidx.lifecycle.LiveData

class ConnectionData : LiveData<ConnectionData>() {

    var mac: String = ""

    var state = ConnectionState.IDLE
        set(value) {
            if (field != value) {
                field = value
                postValue(this)
            }
        }

}