package it.unibo.giacche.contextaware.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import dagger.hilt.android.AndroidEntryPoint
import it.unibo.giacche.contextaware.R
import it.unibo.giacche.contextaware.models.Status
import it.unibo.giacche.contextaware.services.TrackingService
import it.unibo.giacche.contextaware.utils.Constants.ACTION_DISABLE_DUMMY_UPDATES
import it.unibo.giacche.contextaware.utils.Constants.ACTION_ENABLE_DUMMY_UPDATES
import it.unibo.giacche.contextaware.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import it.unibo.giacche.contextaware.utils.Constants.ACTION_STOP_SERVICE
import it.unibo.giacche.contextaware.utils.PermissionsManager
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var serviceActivationSwitch: SwitchCompat
    private lateinit var dummyUpdatesSwitch: SwitchCompat
    private lateinit var gpsPerturbationSwitch: SwitchCompat
    private lateinit var textView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionsManager.requestPermissions(this)
        setContentView(R.layout.activity_main)

        serviceActivationSwitch = findViewById(R.id.service_toggle)
        dummyUpdatesSwitch = findViewById(R.id.dummy_updates)
        gpsPerturbationSwitch = findViewById(R.id.gps_perturbation)
        textView = findViewById(R.id.textView)
        TrackingService.averageNoise.observe(this, {
            when(it.status) {
                Status.ERROR -> textView.text = "Error: " + it.message
                Status.SUCCESS -> textView.text = it.data.toString()
                Status.LOADING -> textView.text = "Loading: " + it.message
            }
        })

        checkLocationTrackerAvailability()

        serviceActivationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sendCommandToService(if (isChecked) ACTION_START_OR_RESUME_SERVICE else ACTION_STOP_SERVICE)
        }

        dummyUpdatesSwitch.setOnCheckedChangeListener { _, isChecked ->
            sendCommandToService(if(isChecked) ACTION_ENABLE_DUMMY_UPDATES else ACTION_DISABLE_DUMMY_UPDATES)
        }
    }

    private fun checkLocationTrackerAvailability() {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            AlertDialog.Builder(this).setMessage(getString(R.string.gps_disabled_message))
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
        else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
            serviceActivationSwitch.isChecked = true
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(this, TrackingService::class.java).also {
            it.action = action
            startService(it)
        }

    // permission callbacks logic

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
            AppSettingsDialog.Builder(this).build().show()
        else PermissionsManager.requestPermissions(this)
    }

    // Nothing to do when permission is granted
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}