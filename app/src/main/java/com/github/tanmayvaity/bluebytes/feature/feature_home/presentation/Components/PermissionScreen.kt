package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.tanmayvaity.bluebytes.R
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.BluetoothPermissionStatus
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionScreen(
    onGrantClick: () -> Unit,
    onGoToSettings: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    permissionStatus: BluetoothPermissionStatus,
    content: @Composable () -> Unit = {}
) {
    // Main Content Card

    var showHome by rememberSaveable {mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        // Icon Background Circle
        if (permissionStatus != BluetoothPermissionStatus.GRANTED
            && permissionStatus != BluetoothPermissionStatus.IN_PROGRESS
            && permissionStatus != BluetoothPermissionStatus.BLUETOOTH_ENABLED
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bluetooth_permission_denied),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(64.dp))
        AnimatedContent(
            targetState = permissionStatus
        ) { permissionStatus ->
            when (permissionStatus) {
                BluetoothPermissionStatus.NOT_PERMITTED -> {
                    showHome = false
                    PermissionCard(
                        title = stringResource(R.string.bluetooth_access_denied),
                        description = stringResource(R.string.bluetooth_access_denied_description),
                        buttonText = stringResource(R.string.bluetooth_permission_denied_btn_text),
                        onButtonClick = onGrantClick,
                        showPrimaryButton = true,
                        secondaryText = stringResource(R.string.bluetooth_permission_denied_privacy),
                        onSecondaryClick = {}
                    )
                }

                BluetoothPermissionStatus.BLUETOOTH_DISABLED -> {
                    showHome = false
                    PermissionCard(
                        title = stringResource(R.string.bluetooth_turned_off),
                        description = stringResource(R.string.bluetooth_turned_off_description),
                        buttonText = stringResource(R.string.bluetooth_turned_off_btn_text),
                        onButtonClick = onGoToSettings,
                        showPrimaryButton = true,
                        secondaryText = stringResource(R.string.bluetooth_turned_learn_more),
                        onSecondaryClick = {}
                    )
                }

                BluetoothPermissionStatus.DEVICE_NOT_CAPABLE -> {
                    showHome = false
                    PermissionCard(
                        title = stringResource(R.string.bluetooth_not_supported_title),
                        description = stringResource(R.string.bluetooth_not_supported_description),
                        buttonText = stringResource(R.string.bluetooth_not_supported_btn_text),
                        onButtonClick = onBack,
                        showPrimaryButton = false,
                        secondaryText = stringResource(R.string.bluetooth_not_supported_go_back),
                        onSecondaryClick = {}
                    )
                }

                BluetoothPermissionStatus.IN_PROGRESS -> {
                    showHome = false
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                else -> {
                    showHome = true
                }
            }
        }
    }
    if(showHome) {
        content()
    }

}


@Composable
fun PermissionCard(
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    showPrimaryButton: Boolean = true,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 32.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(40.dp))

            if (showPrimaryButton) {
                Button(
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            if (secondaryText != null && onSecondaryClick != null) {
                Spacer(Modifier.height(16.dp))

                TextButton(onClick = onSecondaryClick) {
                    Text(
                        text = secondaryText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun PermissionNotPermittedDialogPreview(modifier: Modifier = Modifier) {
    BlueBytesTheme {
        PermissionScreen({}, permissionStatus = BluetoothPermissionStatus.NOT_PERMITTED)
    }
}

@Preview
@Composable
fun PermissionCardPreview() {
    BlueBytesTheme{
        PermissionCard(
            title = "This is a title",
            description = "THis is a description",
            buttonText = "press me",
            onButtonClick = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        )
    }
}