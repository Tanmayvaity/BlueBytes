package com.github.tanmayvaity.bluebytes.feature.feature_home.presentation

sealed class HomeEvents {

    object StartScanning : HomeEvents()
    object StopScanning : HomeEvents()

}