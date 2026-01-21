package com.github.tanmayvaity.bluebytes.core.domain.repository

import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo
import kotlinx.coroutines.flow.StateFlow

interface BluetoothManagerService {
    val pairedDevices : StateFlow<List<BluetoothDeviceInfo>>

    fun startDiscovery()
    fun cancelDiscovery()
}