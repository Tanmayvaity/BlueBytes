package com.github.tanmayvaity.bluebytes.feature.feature_connection.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothMessage
import com.github.tanmayvaity.bluebytes.core.domain.model.ConnectionState

@Composable
fun ConnectionRoot(
    address: String,
    name: String?,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: ConnectionViewModel = hiltViewModel()
) {
    LaunchedEffect(address) {
        viewModel.setConversation(address, name)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    ConnectionScreen(
        modifier = modifier,
        state = state,
        fallbackName = name ?: address,
        onBack = onBack,
        onSend = viewModel::sendMessage,
        onReconnect = viewModel::reconnect,
        onCloseConnection = viewModel::closeConnection
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionScreen(
    modifier: Modifier = Modifier,
    state: ConnectionScreenState = ConnectionScreenState(),
    fallbackName: String = "Connection",
    onBack: () -> Unit = {},
    onSend: (String) -> Unit = {},
    onReconnect: () -> Unit = {},
    onCloseConnection: () -> Unit = {}
) {
    val connection = state.connectionState
    val isConnected = connection is ConnectionState.Connected
    val isConnecting = connection is ConnectionState.Connecting

    // Remember the peer name so the title survives the transition to a closed state.
    var deviceName by rememberSaveable { mutableStateOf<String?>(null) }
    if (connection is ConnectionState.Connected) {
        deviceName = connection.device.name ?: connection.device.address
    }

    var input by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = deviceName ?: fallbackName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isConnected) {
                        IconButton(onClick = onCloseConnection) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Close connection",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            when {
                isConnected -> MessageInputBar(
                    value = input,
                    onValueChange = { input = it },
                    onSend = {
                        onSend(input)
                        input = ""
                    }
                )

                isConnecting -> Text(
                    text = "Connecting…",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                else -> DisconnectedBar(onReconnect = onReconnect)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.messages) { message ->
                MessageBubble(message = message)
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {
    val alignment = if (message.isFromLocalUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor =
        if (message.isFromLocalUser) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (message.isFromLocalUser) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.message,
                color = textColor,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun DisconnectedBar(
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Not connected",
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Medium
        )
        Button(onClick = onReconnect) {
            Text(text = "Reconnect")
        }
    }
}

@Composable
private fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Message") },
            maxLines = 4
        )
        IconButton(
            onClick = onSend,
            enabled = value.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
