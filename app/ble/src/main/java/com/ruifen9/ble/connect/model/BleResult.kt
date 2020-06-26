package com.ruifen9.ble.connect.model

class BleResult(
    val state: Boolean,
    val result: ByteArray?,
    val msg: String?
){
    override fun toString(): String {
        return "BleResult(state=$state, result=${result?.contentToString()}, msg=$msg)"
    }
}