package com.github.tanmayvaity.bluebytes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.HomeRoot
import com.github.tanmayvaity.bluebytes.feature.feature_home.presentation.HomeScreen


@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(Route.Home)
    NavDisplay(
        modifier = modifier,
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
                        HomeRoot()
                    }
                }

                else -> throw RuntimeException("Invalid NavKey.")
            }
        },
    )
}