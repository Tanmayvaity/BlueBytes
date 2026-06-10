package com.github.tanmayvaity.bluebytes.feature.feature_connection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothMessage
import com.github.tanmayvaity.bluebytes.core.domain.model.ConnectionState
import com.github.tanmayvaity.bluebytes.core.domain.repository.BluetoothManagerService
import com.github.tanmayvaity.bluebytes.core.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val bluetoothManagerService: BluetoothManagerService,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConnectionScreenState())
    val state = _state.asStateFlow()

    private val address = MutableStateFlow<String?>(null)
    private var conversationName: String? = null

    init {
        // History comes from the database, so it survives navigation and is per-device.
        address
            .filterNotNull()
            .flatMapLatest { chatRepository.getMessages(it) }
            .onEach { messages -> _state.update { it.copy(messages = messages) } }
            .launchIn(viewModelScope)

        bluetoothManagerService.connectionState
            .onEach { connection -> _state.update { it.copy(connectionState = connection) } }
            .launchIn(viewModelScope)

        // While connected to THIS conversation's device, read incoming messages and persist them.
        // collectLatest restarts the read loop whenever the address or connection state changes.
        viewModelScope.launch {
            combine(address.filterNotNull(), bluetoothManagerService.connectionState) { addr, connection ->
                addr to connection
            }.collectLatest { (addr, connection) ->
                if (connection is ConnectionState.Connected && connection.device.address == addr) {
                    conversationName = connection.device.name ?: conversationName
                    bluetoothManagerService.listenForIncomingMessages().collect { message ->
                        chatRepository.saveMessage(addr, connection.device.name, message)
                    }
                }
            }
        }
    }

    fun setConversation(address: String, name: String?) {
        this.address.value = address
        if (conversationName == null) conversationName = name
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val addr = address.value ?: return
        viewModelScope.launch {
            bluetoothManagerService.sendMessage(text)?.let { sent ->
                chatRepository.saveMessage(addr, conversationName, sent)
            }
        }
    }

    fun reconnect() {
        val addr = address.value ?: return
        bluetoothManagerService.initiateConnection(
            BluetoothDeviceInfo(address = addr, name = conversationName)
        )
    }

    fun closeConnection() {
        bluetoothManagerService.stopConnection()
    }
}

data class ConnectionScreenState(
    val messages: List<BluetoothMessage> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Idle
)
