package com.github.tanmayvaity.bluebytes.navigation

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.annotation.DrawableRes
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import com.github.tanmayvaity.bluebytes.R

enum class TopLevelScreen(
    val route: NavKey,
    val label: String,
    @DrawableRes val icon: Int
) {
    HOME(Route.Home, "Home", R.drawable.ic_discover),
    CHATS(Route.Chats, "Chats", R.drawable.ic_chat),
    SETTINGS(Route.Settings, "Settings", R.drawable.ic_settings)
}

class TopLevelNavigation(private val backStack: NavBackStack<NavKey>) {
    fun navigateTo(destination: NavKey) {
        backStack.clear()
        backStack.add(destination)
    }
}



@Serializable
sealed class Route {

    @Serializable
    data object Home : NavKey, Route()

    @Serializable
    data object Chats : NavKey, Route()

    @Serializable
    data object Settings : NavKey, Route()
}


fun EntryProviderScope<NavKey>.graph(){

}


