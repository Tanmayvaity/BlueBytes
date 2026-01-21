package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation

import android.app.Activity
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components.PermissionScreen
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme
import com.github.tanmayvaity.bluebytes.util.navigateToPermissionSettings
import com.github.tanmayvaity.bluebytes.R
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components.BluetoothDeviceCard
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components.EmptyDevicesPlaceholder
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components.MainFab
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components.ScanStateIndicator
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components.SectionTitle


@Composable
fun HomeRoot(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewmodel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val activity = context as Activity
    val bluetoothManager = context.getSystemService(BluetoothManager::class.java)

    var permissionStatus by rememberSaveable { mutableStateOf(BluetoothPermissionStatus.IN_PROGRESS) }
    val isBluetoothEnabled = rememberBluetoothEnabledState()


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionStatus = if (permissions.values.all { it }) {
            BluetoothPermissionStatus.GRANTED
        } else {
            BluetoothPermissionStatus.NOT_PERMITTED
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        permissionStatus = resolvePermissionStatus(
            context = context,
            activity = activity,
            bluetoothManager = bluetoothManager,
            isBluetoothEnabled = isBluetoothEnabled
        )

        if (permissionStatus == BluetoothPermissionStatus.IN_PROGRESS) {
            requestBluetoothPermissions(permissionLauncher)
        }
    }

    // Update status when Bluetooth state changes
    if (permissionStatus == BluetoothPermissionStatus.GRANTED ||
        permissionStatus == BluetoothPermissionStatus.BLUETOOTH_DISABLED ||
        permissionStatus == BluetoothPermissionStatus.BLUETOOTH_ENABLED
    ) {
        permissionStatus = if (isBluetoothEnabled) {
            BluetoothPermissionStatus.GRANTED
        } else {
            BluetoothPermissionStatus.BLUETOOTH_DISABLED
        }
    }

    if(permissionStatus != BluetoothPermissionStatus.GRANTED){
        viewmodel.onEvent(HomeEvents.StopScanning)
    }

    PermissionScreen(
        permissionStatus = permissionStatus,
        onGrantClick = { context.navigateToPermissionSettings() },
        onGoToSettings = { context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) },
        onBack = onBack
    ) {

        val state by viewmodel.state.collectAsStateWithLifecycle()
        HomeScreen(
            state = state,
            onEvent = viewmodel::onEvent
        )
    }
}

private fun resolvePermissionStatus(
    context: Context,
    activity: Activity,
    bluetoothManager: BluetoothManager,
    isBluetoothEnabled: Boolean
): BluetoothPermissionStatus {
    return when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> BluetoothPermissionStatus.GRANTED
        !bluetoothManager.isBluetoothSupported -> BluetoothPermissionStatus.DEVICE_NOT_CAPABLE
        hasBluetoothPermissions(context) && !isBluetoothEnabled -> BluetoothPermissionStatus.BLUETOOTH_DISABLED
        hasBluetoothPermissions(context) -> BluetoothPermissionStatus.GRANTED
        shouldShowBluetoothRationale(activity) -> BluetoothPermissionStatus.NOT_PERMITTED
        else -> BluetoothPermissionStatus.IN_PROGRESS
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState = HomeState(),
    onEvent: (HomeEvents) -> Unit = {}
) {
    val grey = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val fabIcon =
        if (state.isLoading == LoadState.NOT_STARTED || state.isLoading == LoadState.LOADING_STOPPED) Icons.Default.Search else Icons.Default.Close
    val fabText =
        if (state.isLoading == LoadState.NOT_STARTED || state.isLoading == LoadState.LOADING_STOPPED) "Start Scanning " else "Stop Scanning"

    val stroke = Stroke(width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

//    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
//        onEvent(HomeEvents.StopScanning)
//    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },

        floatingActionButton = {
            MainFab(
                onClick = {
                    if (state.isLoading == LoadState.NOT_STARTED || state.isLoading == LoadState.LOADING_STOPPED) {
                        onEvent(HomeEvents.StartScanning)
                    } else {
                        onEvent(HomeEvents.StopScanning)
                    }
                },
                fabIcon = fabIcon,
                fabText = fabText
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                ScanStateIndicator(
                    loadState = state.isLoading
                )
            }

            if(state.pairedKnownDevices.isNotEmpty()){
                item {
                    SectionTitle(titleRes = R.string.known_devices)
                }
            }

            if(state.pairedKnownDevices.isNotEmpty()) {
                items(state.pairedKnownDevices) {

                    BluetoothDeviceCard(
                        deviceName = it.name ?: stringResource(R.string.unknown_device),
                        deviceAddress = it.address,
                        onConnectClick = {}
                    )
                }
            }

            if(state.unKnownDevices.isEmpty() && state.pairedKnownDevices.isEmpty() && state.isLoading == LoadState.NOT_STARTED) {
                item {
                    EmptyDevicesPlaceholder()
                }
            }

            if(state.unKnownDevices.isNotEmpty()){
                item {
                    SectionTitle(titleRes = R.string.unknown_devices)
                }
            }

            if(state.unKnownDevices.isNotEmpty()) {
                items(state.unKnownDevices) {

                    BluetoothDeviceCard(
                        deviceName = it.name ?: stringResource(R.string.unknown_device),
                        deviceAddress = it.address,
                        onConnectClick = {}
                    )
                }
            }

        }
    }


}

@PreviewLightDark
@Composable
fun HomeScreenPreview(modifier: Modifier = Modifier) {
    val pairedDevices = List(30) {
        BluetoothDeviceInfo(
            address = "123456789",
            name = "Tanmay"
        )
    }

    val unKnownDevices = List(30) {
        BluetoothDeviceInfo(
            address = "123456789",
            name = null
        )
    }

//    val pairedDevices = emptyList<BluetoothDeviceInfo>()
    BlueBytesTheme {
        HomeScreen(
            state = HomeState(
                pairedKnownDevices = pairedDevices,
                unKnownDevices = unKnownDevices
            )
        )
    }
}




