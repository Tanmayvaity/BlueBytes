package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation

import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo

sealed class HomeEvents {

    object StartScanning : HomeEvents()
    object StopScanning : HomeEvents()

    object StartServer : HomeEvents()
    object StopServer : HomeEvents()

    data class ConnectToDevice(val device: BluetoothDeviceInfo) : HomeEvents()
    object Disconnect : HomeEvents()

}
