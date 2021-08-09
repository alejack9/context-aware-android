package it.unibo.giacche.contextaware.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import it.unibo.giacche.contextaware.R
import pub.devrel.easypermissions.EasyPermissions

object PermissionsManager {
    private const val REQUEST_CODE_LOCATION_PERMISSION = 0

    // 'access fine' and 'access coarse' only if build is less then Q
    // Adds 'access background' otherwise
    private val locationPermissions = (arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ) +
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            else "").filter { s -> s != "" }.toTypedArray()

    // All required permissions: location and others
    private val permissions = locationPermissions + arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO
    )

    fun hasLocationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            *locationPermissions
        )

    fun requestPermissions(host: Activity) {
        if (hasLocationPermission(host)) return

        EasyPermissions.requestPermissions(
            host,
            host.getString(R.string.rationale_message),
            REQUEST_CODE_LOCATION_PERMISSION,
            *permissions
        )
    }
}