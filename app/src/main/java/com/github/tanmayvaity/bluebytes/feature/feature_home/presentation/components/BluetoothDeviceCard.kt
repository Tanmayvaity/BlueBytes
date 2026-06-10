package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.tanmayvaity.bluebytes.R
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme

@Composable
fun BluetoothDeviceCard(
    deviceName: String?,
    deviceAddress: String,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().testTag("device_card_${deviceAddress}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_device),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = deviceName ?: stringResource(R.string.unknown_device),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = deviceAddress,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(onClick = onConnectClick) {
                Text(text = stringResource(R.string.connect))
            }
        }
    }
}

@Preview
@Composable
fun BluetoothDeviceCardPreview(modifier: Modifier = Modifier) {
    BlueBytesTheme {
        BluetoothDeviceCard(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
            deviceName = "Tanmay",
            deviceAddress = "123456789",
            onConnectClick = {}
        )
    }
}