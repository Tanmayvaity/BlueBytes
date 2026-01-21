package com.github.tanmayvaity.bluebytes.core.data.mapper


import android.bluetooth.BluetoothDevice
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo


fun BluetoothDevice.toBluetoothDeviceInfo() : BluetoothDeviceInfo {
    return BluetoothDeviceInfo(
        address = address,
        name = name
    )

}