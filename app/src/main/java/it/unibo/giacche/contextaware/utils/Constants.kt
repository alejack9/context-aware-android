package it.unibo.giacche.contextaware.utils

import com.google.android.gms.location.LocationRequest

object Constants {
//    const val DESTINATION_URL = "http://10.8.0.2:3000/locations"
    const val DESTINATION_URL = "https://context-aware-backend.herokuapp.com/locations"

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_ENABLE_DUMMY_UPDATES = "ACTION_ENABLE_DUMMY_UPDATES"
    const val ACTION_DISABLE_DUMMY_UPDATES = "ACTION_DISABLE_DUMMY_UPDATES"
    const val ACTION_ENABLE_GPS_PERTURBATION = "ACTION_ENABLE_GPS_PERTURBATION"
    const val ACTION_DISABLE_GPS_PERTURBATION = "ACTION_DISABLE_GPS_PERTURBATION"
    const val ACTION_DONT_SEND_LOCATIONS = "ACTION_DONT_SEND_LOCATIONS"
    const val ACTION_DO_SEND_LOCATIONS = "ACTION_DO_SEND_LOCATIONS"

    const val LOCATION_UPDATE_INTERVAL = 10_000L
    const val FASTEST_LOCATION_INTERVAL = LOCATION_UPDATE_INTERVAL / 2
    const val LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY
    const val RECORDED_LOCATION_BUFFER_SIZE = 10

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1 // 0 is not a valid id

    const val AUDIO_SAMPLE_RATE = 22050
    const val AUDIO_BUFFER_SIZE = 1024
    const val AUDIO_DURATION = 1_000

    const val SEND_RETRY_TIMEOUT: Long = 2_500
    const val DUMMY_UPDATES = 10
    const val DUMMY_MAX_RADIUS = 0.004
    const val DUMMY_MIN_RADIUS = 0.0005
    const val GPS_PERTURBATOR_DECIMALS = 3
}