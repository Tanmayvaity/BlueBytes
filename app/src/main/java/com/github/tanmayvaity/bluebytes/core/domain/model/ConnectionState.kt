package com.github.tanmayvaity.bluebytes.core.domain.model

sealed interface ConnectionState {
    data object Idle : ConnectionState
    data object Listening : ConnectionState                 // server waiting for a client
    data object Connecting : ConnectionState                // client dialing out
    data class Connected(
        val device: BluetoothDeviceInfo,
        val isServer: Boolean
    ) : ConnectionState
    data class Error(val message: String) : ConnectionState
}
