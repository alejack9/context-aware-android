package it.unibo.giacche.contextaware.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import it.unibo.giacche.contextaware.services.TrackingService
import it.unibo.giacche.contextaware.utils.Constants

class LocationController(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) {
    private lateinit var locationHandler: (Location) -> Unit

    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                locationHandler(result.locations[0])
            }
        }
    }

    // checked by hasLocationPermission but not recognized because not part of main library
    @SuppressLint("MissingPermission")
    fun updateLocationTracking(
        isTracking: Boolean,
        locationHandler: (Location) -> Unit
    ) {
        this.locationHandler = locationHandler
        if (isTracking) {
            val request = LocationRequest.create().apply {
                interval = Constants.LOCATION_UPDATE_INTERVAL
                fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
                priority = Constants.LOCATION_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )
        } else fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}