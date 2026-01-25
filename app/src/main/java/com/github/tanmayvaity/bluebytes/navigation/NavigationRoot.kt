package com.github.tanmayvaity.bluebytes.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.tanmayvaity.bluebytes.feature.feature_chat.presentation.ChatScreen
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.HomeRoot
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.HomeScreen
import com.github.tanmayvaity.bluebytes.feature.feature_settings.presentation.SettingsScreen
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(Route.Home)
    var selectedDestination by rememberSaveable { mutableStateOf(TopLevelScreen.HOME) }
    val topLevelNavigation = remember(backStack) { TopLevelNavigation(backStack) }
    val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
    val nestedScrollState = scrollBehavior.nestedScrollConnection

    Scaffold(
        modifier = modifier
            .nestedScroll(nestedScrollState),
        bottomBar = {
            BottomNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = {screen ->
                    selectedDestination = screen
                    topLevelNavigation.navigateTo(screen.route)
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

        NavDisplay(
            modifier = Modifier
                .padding(innerPadding)

            ,
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when(key) {
                    is Route.Home -> {
                        NavEntry(
                            key = key,
                        ) {
                            HomeRoot(
                                onBack = {
                                    backStack.removeLastOrNull()
                                }

                            )
                        }
                    }

                    is Route.Chats -> {
                        NavEntry(
                            key = key,
                        ) {
                            ChatScreen()
                        }

                    }

                    is Route.Settings -> {
                        NavEntry(
                            key = key
                        ){
                            SettingsScreen()
                        }
                    }

                    else -> error("Invalid NavKey: $key")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomNavigationBar(
    selectedDestination: TopLevelScreen,
    onDestinationSelected: (TopLevelScreen) -> Unit,
    scrollBehavior: BottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
) {
    BottomAppBar(
        windowInsets = NavigationBarDefaults.windowInsets,
        scrollBehavior = scrollBehavior
    ) {
        TopLevelScreen.entries.forEach { screen ->
            NavigationBarItem(
                selected = selectedDestination == screen,
                onClick = {
                    onDestinationSelected(screen)
                },
                icon = {
                    Icon(
                        painter = painterResource(screen.icon),
                        contentDescription = screen.label
                    )
                },
                label = { Text(screen.label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@PreviewLightDark
@Composable
fun BottomNavBarPreview(modifier: Modifier = Modifier) {
    BlueBytesTheme {
        BottomNavigationBar(
            selectedDestination = TopLevelScreen.HOME,
            onDestinationSelected = {}
        )
    }
}