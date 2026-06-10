package com.github.tanmayvaity.bluebytes.core.domain.repository

import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.core.domain.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow

interface BluetoothManagerService {
    val pairedDevices : StateFlow<List<BluetoothDeviceInfo>>
    val connectionState : StateFlow<ConnectionState>

    fun startDiscovery()
    fun cancelDiscovery()

    fun startServer()
    fun stopServer()

    fun initiateConnection(device : BluetoothDeviceInfo)
    fun stopConnection()
}
