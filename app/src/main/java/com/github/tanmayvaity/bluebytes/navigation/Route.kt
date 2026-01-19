package com.github.tanmayvaity.bluebytes.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
sealed class Route {

    @Serializable
    data object Home : NavKey, Route()

    @Serializable
    data object Chats : NavKey, Route()

    @Serializable
    data object Settings : NavKey, Route()
}


