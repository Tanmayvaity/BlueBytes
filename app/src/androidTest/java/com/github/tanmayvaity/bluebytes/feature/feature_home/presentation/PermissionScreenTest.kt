package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.Components.PermissionScreen
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme
import org.junit.Rule
import org.junit.Test

// src/androidTest/java/.../PermissionScreenTest.kt

class PermissionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsHomeScreen_whenGranted() {
        composeTestRule.setContent {
            BlueBytesTheme {
                PermissionScreen(
                    permissionStatus = BluetoothPermissionStatus.GRANTED,
                    onGrantClick = {},
                    onGoToSettings = {},
                    onBack = {}
                ) {
                    HomeScreen(state = HomeState())
                }
            }
        }

        composeTestRule
            .onNodeWithText("Start Scanning", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun showsPermissionUI_whenNotPermitted() {
        composeTestRule.setContent {
            BlueBytesTheme {
                PermissionScreen(
                    permissionStatus = BluetoothPermissionStatus.NOT_PERMITTED,
                    onGrantClick = {},
                    onGoToSettings = {},
                    onBack = {}
                ) {
                    HomeScreen(state = HomeState())
                }
            }
        }

        // Update based on your actual UI text
        composeTestRule
            .onNodeWithTag("Permission not permitted", )
            .assertIsDisplayed()
    }

    @Test
    fun showsBluetoothDisabledUI_whenDisabled() {
        composeTestRule.setContent {
            BlueBytesTheme {
                PermissionScreen(
                    permissionStatus = BluetoothPermissionStatus.BLUETOOTH_DISABLED,
                    onGrantClick = {},
                    onGoToSettings = {},
                    onBack = {}
                ) {
                    HomeScreen(state = HomeState())
                }
            }
        }

        composeTestRule
            .onNodeWithTag("Bluetooth turned off")
            .assertIsDisplayed()
    }

    @Test
    fun showsNotCapableUI_whenDeviceNotCapable() {
        composeTestRule.setContent {
            BlueBytesTheme {
                PermissionScreen(
                    permissionStatus = BluetoothPermissionStatus.DEVICE_NOT_CAPABLE,
                    onGrantClick = {},
                    onGoToSettings = {},
                    onBack = {}
                ) {
                    HomeScreen(state = HomeState())
                }
            }
        }

        composeTestRule
            .onNodeWithText("not supported", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }
}