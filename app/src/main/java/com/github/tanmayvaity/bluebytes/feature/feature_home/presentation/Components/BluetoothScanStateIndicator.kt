package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.tanmayvaity.bluebytes.R
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.LoadState
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme

@Composable
fun ScanStateIndicator(
    loadState: LoadState,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = loadState,
        modifier = modifier
    ) { state ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (state) {
                    LoadState.LOADING -> LoadingIndicator()
                    LoadState.NOT_STARTED -> BluetoothIdleIndicator()
                    LoadState.LOADING_STOPPED -> ScanCompleteIndicator()
                }

                Text(
                    text = when (state) {
                        LoadState.NOT_STARTED -> stringResource(R.string.scanning_not_started)
                        LoadState.LOADING -> stringResource(R.string.scanning)
                        LoadState.LOADING_STOPPED -> stringResource(R.string.scanning_has_stopped)
                    },
                    modifier = Modifier.testTag(
                        when (state) {
                            LoadState.NOT_STARTED -> stringResource(R.string.scanning_not_started)
                            LoadState.LOADING -> stringResource(R.string.scanning)
                            LoadState.LOADING_STOPPED -> stringResource(R.string.scanning_has_stopped)
                        }
                    ),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
private fun LoadingIndicator() {
    CircularProgressIndicator(
        strokeWidth = 3.dp,
        trackColor = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.size(64.dp)
    )
}

@Composable
private fun BluetoothIdleIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bluetooth),
                        contentDescription = "Bluetooth Icon",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = 22.dp, y = 22.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_device),
                        contentDescription = "Device Icon",
                        modifier = Modifier
                            .size(32.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun ScanCompleteIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .border(
                width = 3.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Scan complete",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun ScanCompleteIndicatorPreview(modifier: Modifier = Modifier) {
    BlueBytesTheme {
        ScanCompleteIndicator()
    }
}

@Preview
@Composable
fun BluetoothIdleIndicatorPreview(modifier: Modifier = Modifier) {
    BlueBytesTheme {
        BluetoothIdleIndicator()
    }
}

@Preview
@Composable
private fun ScanStateIndicatorPreview() {
    BlueBytesTheme {
        ScanStateIndicator(loadState = LoadState.LOADING)
    }
}