package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation

import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertCountEquals
import org.junit.Assert.*



import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ============================================
    // Initial State Tests
    // ============================================

    @Test
    fun homeScreen_displaysAppName() {
        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen()
            }
        }

        composeTestRule
            .onNodeWithText("BlueBytes")  // Your app name from strings.xml
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsStartScanningButton_whenNotStarted() {
        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(isLoading = LoadState.NOT_STARTED)
                )
            }
        }

        composeTestRule
            .onNodeWithText("Start Scanning", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsStopScanningButton_whenLoading() {
        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(isLoading = LoadState.LOADING)
                )
            }
        }

        composeTestRule
            .onNodeWithText("Stop Scanning", substring = true)
            .assertIsDisplayed()
    }

    // ============================================
    // Empty State Tests
    // ============================================

    @Test
    fun homeScreen_showsEmptyPlaceholder_whenNoDevices() {
        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(
                        isLoading = LoadState.NOT_STARTED,
                        pairedKnownDevices = emptyList(),
                        unKnownDevices = emptyList()
                    )
                )
            }
        }

        // Verify empty placeholder is shown
        // Update text based on your EmptyDevicesPlaceholder content
        composeTestRule
            .onNodeWithText("No devices nearby", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }

    // ============================================
    // Device List Tests
    // ============================================

    @Test
    fun homeScreen_displaysKnownDevices() {
        val knownDevices = listOf(
            BluetoothDeviceInfo(name = "Galaxy Buds", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = "Car Audio", address = "DD:EE:FF")
        )

        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(pairedKnownDevices = knownDevices)
                )
            }
        }

        composeTestRule.onNodeWithText("Galaxy Buds").assertIsDisplayed()
        composeTestRule.onNodeWithText("Car Audio").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysUnknownDevices() {
        val unknownDevices = listOf(
            BluetoothDeviceInfo(name = null, address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = null, address = "DD:EE:FF")
        )

        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(unKnownDevices = unknownDevices)
                )
            }
        }

        // Based on your code: it.name ?: stringResource(R.string.unknown_device)
        composeTestRule.onNodeWithTag("device_card_AA:BB:CC").assertIsDisplayed()
        composeTestRule.onNodeWithTag("device_card_DD:EE:FF").assertIsDisplayed()

    }

    @Test
    fun homeScreen_showsKnownDevicesSection_whenDevicesExist() {
        val knownDevices = listOf(
            BluetoothDeviceInfo(name = "Test Device", address = "AA:BB:CC")
        )

        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(pairedKnownDevices = knownDevices)
                )
            }
        }

        // Check section title is displayed
        composeTestRule
            .onNodeWithText("Known Devices", substring = true, ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_hidesKnownDevicesSection_whenNoDevices() {
        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(pairedKnownDevices = emptyList())
                )
            }
        }

        composeTestRule
            .onNodeWithText("Known Devices", substring = true, ignoreCase = true)
            .assertDoesNotExist()
    }

    // ============================================
    // Event Tests
    // ============================================

    @Test
    fun homeScreen_triggersStartScanning_onFabClick() {
        var capturedEvent: HomeEvents? = null

        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(isLoading = LoadState.NOT_STARTED),
                    onEvent = { capturedEvent = it }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Start Scanning", substring = true)
            .performClick()

        assert(capturedEvent == HomeEvents.StartScanning)
    }

    @Test
    fun homeScreen_triggersStopScanning_onFabClick_whenLoading() {
        var capturedEvent: HomeEvents? = null

        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(isLoading = LoadState.LOADING),
                    onEvent = { capturedEvent = it }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Stop Scanning", substring = true)
            .performClick()

        assert(capturedEvent == HomeEvents.StopScanning)
    }

    // ============================================
    // Loading State Tests
    // ============================================

    @Test
    fun homeScreen_showsLoadingIndicator_whenScanning() {
        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(isLoading = LoadState.LOADING)
                )
            }
        }

        // Based on your ScanStateIndicator component
        // Update text based on what it displays
        composeTestRule
            .onNodeWithTag("Scanning")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_showsStoppedIndicator_whenLoadingStopped() {
        composeTestRule.setContent {
            BlueBytesTheme {
                HomeScreen(
                    state = HomeState(isLoading = LoadState.LOADING_STOPPED)
                )
            }
        }

        composeTestRule
            .onNodeWithText("Start Scanning", substring = true)
            .assertIsDisplayed()
    }
}