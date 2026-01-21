package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation

import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothDeviceInfo
import com.github.tanmayvaity.bluebytes.core.domain.repository.BluetoothManagerService
import dalvik.annotation.TestTarget
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.invoke
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.nio.file.Files.size

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dispatcher = StandardTestDispatcher()
    val mockPairedDevices = listOf(
        BluetoothDeviceInfo(name = "Galaxy Buds", address = "AA:BB:CC:DD:EE:FF"),
        BluetoothDeviceInfo(name = null, address = "11:22:33:44:55:66"),
        BluetoothDeviceInfo(name = "Car Audio", address = "77:88:99:AA:BB:CC"),
        BluetoothDeviceInfo(name = null, address = "DD:EE:FF:00:11:22")
    )

    private lateinit var bluetoothManagerService: BluetoothManagerService
    private lateinit var viewModel: HomeViewModel
    private lateinit var pairedDevicesFlow: MutableStateFlow<List<BluetoothDeviceInfo>>


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
//        Dispatchers.setMain(dispatcher)
        bluetoothManagerService = mockk(relaxed = true)

        pairedDevicesFlow = MutableStateFlow(emptyList())

        every { bluetoothManagerService.pairedDevices } returns pairedDevicesFlow
        viewModel = HomeViewModel(bluetoothManagerService)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
//        Dispatchers.resetMain()
    }

//    @Test
//    fun getState() {
//    }
//
//    @Test
//    fun getPairedDevices() {
//    }
//
//    @Test
//    fun onEvent() {
//
//    }

    @Test
    fun `initial state has isLoading as NOT_STARTED`() {
        assertEquals(LoadState.NOT_STARTED, viewModel.state.value.isLoading)
    }


    @Test
    fun `StartScanning sets isLoading to LOADING`() = runTest {
        viewModel.onEvent(HomeEvents.StartScanning)
        assertEquals(LoadState.LOADING, viewModel.state.value.isLoading)
    }

    @Test
    fun `StartScanning calls startDiscovery`() = runTest {
        viewModel.onEvent(HomeEvents.StartScanning)

        advanceUntilIdle()

        coVerify { bluetoothManagerService.startDiscovery() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `LoadState remains LOADING for 20 seconds after StartScanning is called`() = runTest {
        viewModel.onEvent(HomeEvents.StartScanning)

        advanceTimeBy(19_999)

        assertEquals(LoadState.LOADING, viewModel.state.value.isLoading)

    }

    @Test
    fun `Start Scanning calls Stop Scanning after 20 seconds of being called`() = runTest {
        viewModel.onEvent(HomeEvents.StartScanning)

        advanceTimeBy(20_001)

        coVerify { bluetoothManagerService.cancelDiscovery() }
    }

    @Test
    fun `LoadState becomes NOT_STARTED 1 second after StopScanning`() = runTest {
        viewModel.onEvent(HomeEvents.StartScanning)
        advanceTimeBy(20_001)


        assertEquals(LoadState.LOADING_STOPPED, viewModel.state.value.isLoading)



        advanceTimeBy(1_001)

        assertEquals(LoadState.NOT_STARTED, viewModel.state.value.isLoading)
    }


    @Test
    fun `StartScanning full state timeline`() = runTest {
        // Initial
        assertEquals(LoadState.NOT_STARTED, viewModel.state.value.isLoading)

        // Start
        viewModel.onEvent(HomeEvents.StartScanning)
        assertEquals(LoadState.LOADING, viewModel.state.value.isLoading)

        // Still loading at 19 seconds
        advanceTimeBy(19_000)
        assertEquals(LoadState.LOADING, viewModel.state.value.isLoading)

        // Auto-stop at 20 seconds
        advanceTimeBy(1_001)
        assertEquals(LoadState.LOADING_STOPPED, viewModel.state.value.isLoading)

        // Back to NOT_STARTED after 1 more second
        advanceTimeBy(1_001)
        assertEquals(LoadState.NOT_STARTED, viewModel.state.value.isLoading)
    }


    @Test
    fun `StopScanning calls cancelDiscovery when LOADING`() = runTest {
        viewModel.onEvent(HomeEvents.StartScanning)

        assertEquals(LoadState.LOADING, viewModel.state.value.isLoading)

        viewModel.onEvent(HomeEvents.StopScanning)
        advanceUntilIdle()

        coVerify { bluetoothManagerService.cancelDiscovery() }
    }

    @Test
    fun `StopScanning does not call cancelDiscovery when NOT_STARTED`() = runTest {
        assertEquals(LoadState.NOT_STARTED, viewModel.state.value.isLoading)

        viewModel.onEvent(HomeEvents.StopScanning)

        coVerify(exactly = 0) { bluetoothManagerService.cancelDiscovery() }
    }

    @Test
    fun `StopScanning does not change state when NOT_STARTED`() = runTest {
        assertEquals(LoadState.NOT_STARTED, viewModel.state.value.isLoading)

        viewModel.onEvent(HomeEvents.StopScanning)

        // Even after advancing time, state should remain NOT_STARTED
        advanceTimeBy(2000)

        assertEquals(LoadState.NOT_STARTED, viewModel.state.value.isLoading)
    }


    @Test
    fun `initial state has empty device lists`() = runTest {

//        val viewModel = createViewModel()
        assertEquals(emptyList<BluetoothDeviceInfo>(), viewModel.state.value.pairedKnownDevices)
        assertEquals(emptyList<BluetoothDeviceInfo>(), viewModel.state.value.unKnownDevices)
    }

    // ============================================
    // Known vs Unknown Filtering Tests
    // ============================================

    @Test
    fun `devices with name are added to pairedKnownDevices`() = runTest {

        val knownDevice = BluetoothDeviceInfo(name = "Galaxy Buds", address = "AA:BB:CC")

        pairedDevicesFlow.update { listOf(knownDevice) }

        advanceUntilIdle()


        assertEquals(1, viewModel.state.value.pairedKnownDevices.size)
        assertEquals(0, viewModel.state.value.unKnownDevices.size)
        assertEquals("Galaxy Buds", viewModel.state.value.pairedKnownDevices[0].name)
    }

    @Test
    fun `devices with no name are added to unKnownDevices`() = runTest() {
        val unknownDevice = BluetoothDeviceInfo(name = null, address = "AA:BB:CC")
        pairedDevicesFlow.update { listOf(unknownDevice) }

        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.unKnownDevices.size)
        assertEquals(0, viewModel.state.value.pairedKnownDevices.size)
        assertEquals(null, viewModel.state.value.unKnownDevices[0].name)

    }

    @Test
    fun `mixed devices are separated correctly`() = runTest {
        val devices = listOf(
            BluetoothDeviceInfo(name = "Known 1", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = null, address = "DD:EE:FF"),
            BluetoothDeviceInfo(name = "Known 2", address = "11:22:33"),
            BluetoothDeviceInfo(name = null, address = "44:55:66")
        )

        pairedDevicesFlow.value = devices
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.pairedKnownDevices.size)
        assertEquals(2, viewModel.state.value.unKnownDevices.size)
    }

    // ============================================
    // Deduplication Tests
    // ============================================

    @Test
    fun `duplicate addresses are filtered out`() = runTest {
        val devices = listOf(
            BluetoothDeviceInfo(name = "Device 1", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = "Device 1 Copy", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = "Device 2", address = "DD:EE:FF")
        )

        pairedDevicesFlow.value = devices
        advanceUntilIdle()

        assertEquals(2, viewModel.state.value.pairedKnownDevices.size)
    }

    @Test
    fun `first device with duplicate address is kept`() = runTest {
        val devices = listOf(
            BluetoothDeviceInfo(name = "First", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = "Second", address = "AA:BB:CC")
        )

        pairedDevicesFlow.value = devices
        advanceUntilIdle()
        assertEquals("First", viewModel.state.value.pairedKnownDevices[0].name)
    }

    // ============================================
    // State Replacement Tests
    // ============================================

    @Test
    fun `new emission replaces previous state`() = runTest {
        // First emission
        pairedDevicesFlow.value = listOf(
            BluetoothDeviceInfo(name = "Old Device", address = "AA:BB:CC")
        )
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.pairedKnownDevices.size)
        assertEquals("Old Device", viewModel.state.value.pairedKnownDevices[0].name)

        // Second emission replaces completely
        pairedDevicesFlow.value = listOf(
            BluetoothDeviceInfo(name = "New Device", address = "DD:EE:FF")
        )
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.pairedKnownDevices.size)
        assertEquals("New Device", viewModel.state.value.pairedKnownDevices[0].name)
    }

    @Test
    fun `re-emission of same devices does not create duplicates`() = runTest {
        val devices = listOf(
            BluetoothDeviceInfo(name = "Device 1", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = "Device 2", address = "DD:EE:FF")
        )

        // First emission
        pairedDevicesFlow.value = devices
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.pairedKnownDevices.size)

        // Same emission again (simulates returning to screen)
        pairedDevicesFlow.value = devices
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.pairedKnownDevices.size)
    }

    @Test
    fun `empty emission clears state`() = runTest {
        // First emission with devices
        pairedDevicesFlow.value = listOf(
            BluetoothDeviceInfo(name = "Device", address = "AA:BB:CC")
        )
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.pairedKnownDevices.size)

        // Empty emission
        pairedDevicesFlow.value = emptyList()
        advanceUntilIdle()

        assertEquals(0, viewModel.state.value.pairedKnownDevices.size)
        assertEquals(0, viewModel.state.value.unKnownDevices.size)
    }

    // ============================================
    // Scanning Flow Tests
    // ============================================

    @Test
    fun `scanning discovers new devices`() = runTest {
        // Initial paired devices
        pairedDevicesFlow.value = listOf(
            BluetoothDeviceInfo(name = "Already Paired", address = "AA:BB:CC")
        )
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.pairedKnownDevices.size)

        // Start scanning
        viewModel.onEvent(HomeEvents.StartScanning)

        // New device discovered
        pairedDevicesFlow.value = listOf(
            BluetoothDeviceInfo(name = "Already Paired", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = "Newly Found", address = "DD:EE:FF")
        )
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.pairedKnownDevices.size)
    }

    @Test
    fun `scanning with duplicate addresses does not create duplicates`() = runTest {
        pairedDevicesFlow.value = listOf(
            BluetoothDeviceInfo(name = "Device 1", address = "AA:BB:CC")
        )
        advanceUntilIdle()

        viewModel.onEvent(HomeEvents.StartScanning)

        // Emission with same device + duplicate + new device
        pairedDevicesFlow.value = listOf(
            BluetoothDeviceInfo(name = "Device 1", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = "Device 1 Again", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = "Device 2", address = "DD:EE:FF")
        )
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.pairedKnownDevices.size)
    }

    // ============================================
    // Edge Cases
    // ============================================

    @Test
    fun `all unknown devices`() = runTest {
        val devices = listOf(
            BluetoothDeviceInfo(name = null, address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = null, address = "DD:EE:FF")
        )

        pairedDevicesFlow.value = devices

        advanceUntilIdle()

        assertEquals(0, viewModel.state.value.pairedKnownDevices.size)
        assertEquals(2, viewModel.state.value.unKnownDevices.size)
    }

    @Test
    fun `all known devices`() = runTest {
        val devices = listOf(
            BluetoothDeviceInfo(name = "Device 1", address = "AA:BB:CC"),
            BluetoothDeviceInfo(name = "Device 2", address = "DD:EE:FF")
        )

        pairedDevicesFlow.value = devices
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.pairedKnownDevices.size)
        assertEquals(0, viewModel.state.value.unKnownDevices.size)
    }




    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(bluetoothManagerService)
    }

}


@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}


