package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.core.domain.model.ConnectionState
import com.github.tanmayvaity.bluebytes.core.domain.repository.BluetoothManagerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bluetoothManagerService: BluetoothManagerService
) : ViewModel() {
    companion object {
        private const val TAG = "HomeViewModel"
    }
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()


    val pairedDevices = bluetoothManagerService
        .pairedDevices
        .onStart {
//            _state.update { it.copy(isLoading = LoadState.LOADING) }
        }
        .onEach { devices ->
//            Log.d(TAG, "paired Devices : ${devices}")
//            Log.d(TAG, "paired Devices : $devices")

            // Deduplicate by address within the emission itself
            val uniqueDevices = devices.distinctBy { it.address }

            val unknownDevices = uniqueDevices.filter { it.name == null }
            val knownDevices = uniqueDevices.filter { it.name != null }

            _state.update {
                it.copy(
                    unKnownDevices = unknownDevices,
                    pairedKnownDevices = knownDevices
                )
            }
        }
        .onCompletion {
//            _state.update { it.copy(isLoading = false) }
        }
        .launchIn(viewModelScope)

    private val connectionState = bluetoothManagerService
        .connectionState
        .onEach { connection ->
            _state.update { it.copy(connectionState = connection) }
        }
        .launchIn(viewModelScope)

//    init {
//        viewModelScope.launch {
//            delay(15000)
//            onEvent(HomeEvents.StopScanning)
//            _state.update { it.copy(isLoading = LoadState.LOADING_STOPPED) }
//        }
//    }




    fun onEvent(event: HomeEvents) {
        when (event) {
            HomeEvents.StartScanning -> {
                _state.update { it.copy(isLoading = LoadState.LOADING) }
                viewModelScope.launch {
                    bluetoothManagerService.startDiscovery()
                    delay(20000)
                    onEvent(HomeEvents.StopScanning)
                }

            }

            HomeEvents.StopScanning -> {
                viewModelScope.launch {

                    if(state.value.isLoading == LoadState.LOADING){
                        bluetoothManagerService.cancelDiscovery()
                        _state.update { it.copy(isLoading = LoadState.LOADING_STOPPED) }
                        delay(1000)
                        _state.update { it.copy(isLoading = LoadState.NOT_STARTED) }
                    }


                }
            }

            HomeEvents.StartServer -> {
                bluetoothManagerService.startServer()
            }

            HomeEvents.StopServer -> {
                bluetoothManagerService.stopServer()
            }

            is HomeEvents.ConnectToDevice -> {
                bluetoothManagerService.initiateConnection(event.device)
            }

            HomeEvents.Disconnect -> {
                bluetoothManagerService.stopConnection()
            }
        }
    }
}


data class HomeState(
    val isLoading: LoadState = LoadState.NOT_STARTED,
    val unKnownDevices : List<BluetoothDeviceInfo> = emptyList(),
    val pairedKnownDevices : List<BluetoothDeviceInfo> = emptyList(),
    val connectionState : ConnectionState = ConnectionState.Idle
)

enum class LoadState {
    NOT_STARTED,
    LOADING,
    LOADING_STOPPED
}