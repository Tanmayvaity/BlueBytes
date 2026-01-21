package com.github.tanmayvaity.bluebytes.core.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.github.tanmayvaity.bluebytes.core.data.mapper.toBluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.core.domain.repository.BluetoothManagerService
import com.github.tanmayvaity.bluebytes.util.hasPermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Suppress("MissingPermission")
class BluetoothManagerServiceImpl @Inject constructor(
    private val context: Context
) : BluetoothManagerService {

    private val bluetoothManager by lazy { context.getSystemService(BluetoothManager::class.java) as BluetoothManager }

    private val adapter by lazy {
        bluetoothManager?.adapter
    }


    private val _pairedDevices = MutableStateFlow(emptyList<BluetoothDeviceInfo>())
    override val pairedDevices: StateFlow<List<BluetoothDeviceInfo>>
        get() = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _pairedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceInfo()
            if (newDevice in devices) devices else devices + newDevice
            devices + newDevice
        }

    }

    init {
        updatePairedDevices()
    }

    override fun startDiscovery() {
        if (!hasBluetoothPermission()) return

        context.registerReceiver(
            foundDeviceReceiver,
            android.content.IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        adapter?.startDiscovery()
    }

    override fun cancelDiscovery() {
        if (!hasBluetoothPermission()) return
        adapter?.cancelDiscovery()
        Log.d(TAG, "cancelDiscovery: discovery cancelled")
    }

    private fun updatePairedDevices() {
        if (!hasBluetoothPermission()) return
        adapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceInfo() }
            ?.let { _pairedDevices.update { it } }

    }


    private fun hasBluetoothPermission(): Boolean {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
            return true
        }
        return context.hasPermission(Manifest.permission.BLUETOOTH_SCAN) && context.hasPermission(
            Manifest.permission.BLUETOOTH_CONNECT)
    }

    companion object {
        private const val TAG = "BluetoothManagerServiceImpl"
    }


}