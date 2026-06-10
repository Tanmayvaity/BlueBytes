package com.github.tanmayvaity.bluebytes.core.domain.model

typealias BluetoothDeviceInfo = BluetoothDevice

data class BluetoothDevice(
    val address : String,
    val name : String?
)

