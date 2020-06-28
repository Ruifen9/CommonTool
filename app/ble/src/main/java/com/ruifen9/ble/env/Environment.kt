package com.ruifen9.ble.env

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