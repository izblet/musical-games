package com.example.musicalgames.wrappers.bluetooth
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.musicalgames.wrappers.PermissionsUtil

abstract class BluetoothConnectionManager(protected var context: Context, activityRegistry: ActivityResultRegistry) {
    private var permissionLauncher: ActivityResultLauncher<Array<String>>
    protected var permissions: Array<String>
    init {
        permissionLauncher= PermissionsUtil.registerLauncher(activityRegistry)
        permissions = if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADMIN)
        } else arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION)
    }
    abstract fun bluetoothUnsubscribe()
    abstract fun bluetoothSubscribe(listener: BluetoothEventListener)
    abstract fun releaseResources()
    fun listPermissions(): Array<String> {
        return permissions
    }
    fun checkPermissions(): Boolean {
        return PermissionsUtil.checkAllPermissions(permissions, context)
    }
    fun enableBluetooth() {
        PermissionsUtil.askMissingPermissions(permissions, context, permissionLauncher)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }

    abstract fun sendMessage(message: Int)

    private val requestBluetooth: ActivityResultLauncher<Intent> =
        activityRegistry.register(
            "bluetooth_enable",
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                //granted
            } else {
                //deny
            }
        }
    abstract fun connected():Boolean
}
