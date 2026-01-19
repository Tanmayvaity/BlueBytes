package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.github.tanmayvaity.bluebytes.core.presentation.PermissionDeniedDialog
import com.github.tanmayvaity.bluebytes.core.presentation.PermissionRationaleDialog
import com.github.tanmayvaity.bluebytes.core.presentation.PermissionScreen
import com.github.tanmayvaity.bluebytes.util.navigateToPermissionSettings


@Composable
fun HomeRoot(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as Activity
    var showHomeScreen by rememberSaveable() {mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current


    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.map { (permission, granted) ->
            if (!granted) {
                // show dialog
                
            }
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        requestBluetoothPermissionLogic(
            onGranted = {
                showHomeScreen = true
            },
            onRationale = {
                showHomeScreen = false
            },
            context = context,
            activity = activity,
            bluetoothPermissionLauncher = bluetoothPermissionLauncher
        )
    }

    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
        showHomeScreen = true
    }

    if(showHomeScreen){
        HomeScreen()
    }else{
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            PermissionScreen(
                onGrantClick = {
                    context.navigateToPermissionSettings()
                },
            )
        }
    }

}


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Home Screen"
        )
    }
}



fun requestBluetoothPermissionLogic(
    onGranted : () -> Unit = {},
    onRationale : () -> Unit = {},
    context : Context,
    activity : Activity,
    bluetoothPermissionLauncher : ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
){
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED

            -> {
            onGranted()
        }

        activity.shouldShowRequestPermissionRationale(
            Manifest.permission.BLUETOOTH_SCAN
        )  || activity.shouldShowRequestPermissionRationale(
            Manifest.permission.BLUETOOTH_ADVERTISE
        ) || activity.shouldShowRequestPermissionRationale(
            Manifest.permission.BLUETOOTH_CONNECT
        ) -> {
            // should show rationale
            onRationale()
        }

        else -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                bluetoothPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                )
            }
        }

    }
}



