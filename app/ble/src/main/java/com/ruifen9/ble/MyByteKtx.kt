package com.ruifen9.ble

import java.nio.ByteBuffer
import java.util.*

fun Byte.unsignedByteToInt(): Int {
    return this.toInt().and(0xff)
}

fun unsignedByteToInt(byte1: Byte, byte2: Byte): Int {
    return (byte1.unsignedByteToInt() shl 8).or(byte2.unsignedByteToInt())
}

fun ByteArray.toHexString(): String {
    if (isEmpty()) return "Empty Byte Array"
    val sb = StringBuilder()
    for (i in indices) {
        val tmp = this[i].unsignedByteToInt()
        sb.append(String.format("%02X", tmp))
    }
    return sb.toString()
}

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}

fun Int.intToBytes(): ByteArray {
    val b = ByteBuffer.allocate(4)
    b.putInt(this)
    return b.array()
}

//"aa:bb:cc:dd:ee:ff"->bytes=new byte[]{0xaa,0xbb,0xcc,0xdd,0xee,0xff}
fun String.macToByteArray(): ByteArray {
    return try {
        val splitResult = this.split(":")
        val temp = ArrayList<Byte>()
        for (s in splitResult) {
            temp.add(s.toInt(16).toByte())
        }
        temp.toByteArray()
    } catch (e: Exception) {
        byteArrayOf()
    }
}