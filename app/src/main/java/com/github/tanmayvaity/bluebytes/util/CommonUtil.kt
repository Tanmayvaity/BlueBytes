package com.github.tanmayvaity.bluebytes.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun Context.navigateToPermissionSettings(){
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts("package", this.getPackageName(), null)
    intent.data = uri
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    this.startActivity(intent)
}

fun Context.hasPermission(permission: String): Boolean {
    return this.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}