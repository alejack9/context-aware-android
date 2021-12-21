package it.unibo.giacche.contextaware.utils

import com.google.android.gms.location.LocationRequest

object Constants {
    const val DESTINATION_URL = "http://192.168.1.89:3001/"
    const val GET_ENDPOINT = DESTINATION_URL
    const val SEND_ENDPOINT = DESTINATION_URL
//    const val DESTINATION_URL = "https://context-aware-backend.herokuapp.com/"

    const val ID_ENDPOINT = DESTINATION_URL + "id"
    const val SHARED_PREFS_NAME = "it.unibo.giacche.contextaware"
    const val REQ_ID_KEY = "$SHARED_PREFS_NAME.ID"

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
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


    const val DEFAULT_GPS_PERT_ENABLED = false
    const val DEFAULT_GPS_PERT_REAL_DECIMALS = 3
    const val DEFAULT_DUMMY_UPDATES_ENABLED = false
    const val DEFAULT_D_UP_MIN: Double = 1500.0
    const val DEFAULT_D_UP_RANGE: Double = 250.0
    const val DEFAULT_D_UP_COUNT = 10
    const val DEFAULT_SPATIAL_CLOAKING_ENABLED = false
    const val DEFAULT_SP_CL_TIMEOUT = 5000
    const val DEFAULT_SP_CL_RANGE: Double = 500.0
    const val DEFAULT_SP_CL_K = 2
    const val DEFAULT_ALPHA_ENABLED = false
    const val DEFAULT_ALPHA_VALUE = 0.5
}