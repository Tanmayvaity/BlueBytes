package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme
import com.github.tanmayvaity.bluebytes.R
private val DarkPurple = Color(0xFF1E0A3C)
private val MediumPurple = Color(0xFF3D1D72)
private val LightPurple = Color(0xFFE8DEF8)
private val VeryLightPurple = Color(0xFFF3EEFF)
private val GreenConnected = Color(0xFF4CAF50)
private val AccentPurple = Color(0xFF7C4DFF)

@Composable
fun PairedDeviceCard(
    deviceName: String,
    macAddress: String,
    isConnected: Boolean = true,
    onOpenChatClick: () -> Unit = {},
    onDisconnectClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = if (isConnected) GreenConnected else Color.Gray,
        label = "statusColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top row: Connection status + Device name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Connection status
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = if (isConnected) "Connected" else "Disconnected",
                            color = statusColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Device name
                    Text(
                        text = deviceName,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // MAC address + Battery
                    Text(
                        text = "$macAddress",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Tooltip-style label
//                Surface(
//                    shape = RoundedCornerShape(8.dp),
//                    color = DarkPurple,
//                    shadowElevation = 2.dp
//                ) {
//                    Text(
//                        text = "Stitch - Design with AI",
//                        color = Color.White,
//                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Medium,
//                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
//                    )
//                }


            }

            // Bottom row: Open Chat button + Settings icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Open Chat button
                Button(
                    onClick = onOpenChatClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chat), // Replace with chat icon
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open Chat",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }

                // Disconnect button
                IconButton(
                    onClick = onDisconnectClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Disconnect",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BluetoothDeviceCardPreview() {
    BlueBytesTheme {
        PairedDeviceCard(
            deviceName = "WH-1000XM5",
            macAddress = "AA:BB:CC:DD:EE:FF",
            isConnected = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}