package com.github.tanmayvaity.bluebytes.feature.feature_connection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothMessage
import com.github.tanmayvaity.bluebytes.core.domain.model.ConnectionState
import com.github.tanmayvaity.bluebytes.core.domain.repository.BluetoothManagerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val bluetoothManagerService: BluetoothManagerService
) : ViewModel() {

    private val _state = MutableStateFlow(ConnectionScreenState())
    val state = _state.asStateFlow()

    init {
        bluetoothManagerService.connectionState
            .onEach { connection ->
                _state.update { it.copy(connectionState = connection) }
            }
            .launchIn(viewModelScope)

        // Messages live only here (nav-entry scoped) - no persistence.
        bluetoothManagerService.listenForIncomingMessages()
            .onEach { message ->
                _state.update { it.copy(messages = it.messages + message) }
            }
            .launchIn(viewModelScope)
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            bluetoothManagerService.sendMessage(text)?.let { sent ->
                _state.update { it.copy(messages = it.messages + sent) }
            }
        }
    }

    fun closeConnection() {
        bluetoothManagerService.stopConnection()
    }
}

data class ConnectionScreenState(
    val messages: List<BluetoothMessage> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Idle
)
