package com.github.tanmayvaity.bluebytes.core.data.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.github.tanmayvaity.bluebytes.core.data.mapper.toBluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothMessage
import com.github.tanmayvaity.bluebytes.core.domain.model.ConnectionState
import com.github.tanmayvaity.bluebytes.core.domain.repository.BluetoothManagerService
import com.github.tanmayvaity.bluebytes.util.hasPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlin.uuid.Uuid

@Suppress("MissingPermission")
class BluetoothManagerServiceImpl @Inject constructor(
    private val context: Context
) : BluetoothManagerService {

    private val bluetoothManager by lazy { context.getSystemService(BluetoothManager::class.java) as BluetoothManager }

    private val bluetoothUuid: UUID = UUID.fromString("6ded0daa-a834-4615-90f1-229ebf3ccc25")

    // Created fresh on each startServer() — a server socket cannot be reused after close().
    private var serverSocket: BluetoothServerSocket? = null

    private var clientSocket: BluetoothSocket? = null


    private var acceptJob: Job? = null
    private var clientJob : Job? = null

    private val adapter by lazy {
        bluetoothManager?.adapter
    }


    private val _pairedDevices = MutableStateFlow(emptyList<BluetoothDeviceInfo>())
    override val pairedDevices: StateFlow<List<BluetoothDeviceInfo>>
        get() = _pairedDevices.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Idle)
    override val connectionState: StateFlow<ConnectionState>
        get() = _connectionState.asStateFlow()

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

    override fun startServer() {
        if (!hasBluetoothPermission()) return

        _connectionState.update { ConnectionState.Listening }
        acceptJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                serverSocket = adapter?.listenUsingInsecureRfcommWithServiceRecord(
                    "BlueBytes",
                    bluetoothUuid
                )
                // accept() blocks until a client connects (single connection).
                val socket = serverSocket?.accept()
                serverSocket?.close()
                if (socket != null) {
                    clientSocket = socket
                    Log.d(TAG, "startServer: client connected ${socket.remoteDevice.address}")
                    _connectionState.update {
                        ConnectionState.Connected(
                            device = socket.remoteDevice.toBluetoothDeviceInfo(),
                            isServer = true
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Accept failed", e)
                serverSocket?.close()
                _connectionState.update { ConnectionState.Error(e.message ?: "Server failed") }
            }
        }
    }

    override fun stopServer() {
        acceptJob?.cancel()
        try {
            serverSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error closing server socket : ${e.message}")
        }
        serverSocket = null
        _connectionState.update { ConnectionState.Idle }
    }

    override fun initiateConnection(device: BluetoothDeviceInfo) {
        if (!hasBluetoothPermission()) return

        _connectionState.update { ConnectionState.Connecting }
        clientJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                // Discovery slows down / breaks an RFCOMM connect, so cancel it first.
                adapter?.cancelDiscovery()
                val remoteDevice = adapter?.getRemoteDevice(device.address)
                clientSocket = remoteDevice?.createRfcommSocketToServiceRecord(bluetoothUuid)
                clientSocket?.connect()
                Log.d(TAG, "initiateConnection: connected to ${device.address}")
                _connectionState.update {
                    ConnectionState.Connected(device = device, isServer = false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "initiateConnection failed", e)
                try {
                    clientSocket?.close()
                } catch (_: IOException) {
                }
                clientSocket = null
                _connectionState.update { ConnectionState.Error(e.message ?: "Connection failed") }
            }
        }
    }

    override fun stopConnection() {
        try {
            clientSocket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error While closing connection : ${e.message}")
        }
        clientSocket = null
        clientJob?.cancel()
        _connectionState.update { ConnectionState.Idle }
    }

    override fun listenForIncomingMessages(): Flow<BluetoothMessage> {
        val socket = clientSocket ?: return emptyFlow()
        return flow {
            val buffer = ByteArray(1024)
            while (currentCoroutineContext().isActive) {
                val byteCount = try {
                    socket.inputStream.read(buffer)
                } catch (e: IOException) {
                    // Remote side closed or the link dropped.
                    Log.d(TAG, "listenForIncomingMessages: connection lost - ${e.message}")
                    _connectionState.update { ConnectionState.Idle }
                    break
                }
                if (byteCount <= 0) break
                emit(
                    BluetoothMessage(
                        message = String(buffer, 0, byteCount),
                        isFromLocalUser = false
                    )
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun sendMessage(message: String): BluetoothMessage? {
        val socket = clientSocket ?: return null
        return try {
            withContext(Dispatchers.IO) {
                socket.outputStream.write(message.toByteArray())
            }
            BluetoothMessage(message = message, isFromLocalUser = true)
        } catch (e: IOException) {
            Log.e(TAG, "sendMessage failed : ${e.message}")
            null
        }
    }

    private fun updatePairedDevices() {
        if (!hasBluetoothPermission()) return
        adapter
            ?.bondedDevices
            ?.map { it.toBluetoothDeviceInfo() }
            ?.let { _pairedDevices.update { it } }

    }


    private fun hasBluetoothPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return true
        }
        return context.hasPermission(Manifest.permission.BLUETOOTH_SCAN) && context.hasPermission(
            Manifest.permission.BLUETOOTH_CONNECT
        )
    }

    companion object {
        private const val TAG = "BluetoothManagerServiceImpl"
    }


}