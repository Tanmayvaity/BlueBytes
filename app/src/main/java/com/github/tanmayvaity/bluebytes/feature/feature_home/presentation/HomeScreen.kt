package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.tanmayvaity.bluebytes.core.presentation.PermissionDeniedDialog
import com.github.tanmayvaity.bluebytes.core.presentation.PermissionRationaleDialog
import com.github.tanmayvaity.bluebytes.core.presentation.PermissionScreen
import com.github.tanmayvaity.bluebytes.util.navigateToPermissionSettings




@Composable
fun HomeRoot(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
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

    PermissionScreen(
        permissionStatus = permissionStatus,
        onGrantClick = { context.navigateToPermissionSettings() },
        onGoToSettings = { context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) },
        onBack = onBack
    ) {
        HomeScreen()
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


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home Screen"
        )
    }
}




