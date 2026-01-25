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
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

val BluetoothManager.isBluetoothSupported: Boolean
    get() = adapter != null

val BluetoothManager.isBluetoothEnabled: Boolean
    get() = adapter?.isEnabled == true

@Composable
fun rememberBluetoothEnabledState(): Boolean {
    val context = LocalContext.current
    val bluetoothAdapter = remember {
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
    }

    var isEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                        BluetoothAdapter.STATE_ON -> isEnabled = true
                        BluetoothAdapter.STATE_OFF -> isEnabled = false
                    }
                }
            }
        }

        context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        onDispose { context.unregisterReceiver(receiver) }
    }

    return isEnabled
}

@Composable
fun rememberDeviceDiscoverableState() : Boolean {

    val context = LocalContext.current

    var isDeviceDiscoverable by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action == BluetoothAdapter.ACTION_SCAN_MODE_CHANGED){
                    isDeviceDiscoverable =
                        when(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR)){
                            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> true
                            else -> false

                        }

                }

            }

        }
        context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED))

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }


    return isDeviceDiscoverable
}



fun hasBluetoothPermissions(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true

    return listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT
    ).all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
}

fun shouldShowBluetoothRationale(activity: Activity): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false

    return listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT
    ).any { activity.shouldShowRequestPermissionRationale(it) }
}

fun requestBluetoothPermissions(
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        launcher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        )
    }
}
