package com.example.musicalgames.utils

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

object PermissionsUtil {
    fun registerLauncher(activityResultRegistry: ActivityResultRegistry) : ActivityResultLauncher<Array<String>> {
        return activityResultRegistry.register(
                "multiple_permissions",
                ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    Log.d("test006", "${it.key} = ${it.value}")
                }
            }
    }
    fun checkPermission(permission: String, context: Context): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return false

        }
        return true
    }
    fun checkAllPermissions(permissions: Array<String>, context: Context):Boolean {
        for(permission in permissions)
            if(!checkPermission(permission, context))
                return false
        return true
    }
    fun askPermissions(permissions: Array<String>, permissionLauncher: ActivityResultLauncher<Array<String>>) {
        permissionLauncher.launch(permissions)
    }

    fun askMissingPermissions(permissions: Array<String>, context: Context, permissionLauncher: ActivityResultLauncher<Array<String>>) {
        var missingPermissions = mutableListOf<String>()
        for(permission in permissions)
            if(!checkPermission(permission, context))
                missingPermissions.add(permission)

        if(missingPermissions.size>0)
            askPermissions(missingPermissions.toTypedArray(), permissionLauncher)
    }


}