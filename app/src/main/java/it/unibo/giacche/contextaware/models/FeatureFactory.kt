package it.unibo.giacche.contextaware.models

import android.location.Location
import org.geojson.Feature
import org.geojson.Point
import it.unibo.giacche.contextaware.utils.Constants

object FeatureFactory {
    fun build(location : Location, noise: Double) = Feature().apply {
        geometry = Point(location.longitude, location.latitude)
        properties = mapOf(
            "timeStamp" to location.time,
            "noiseLevel" to noise,
            "dummyLocation" to Constants.DUMMY_UPDATES,
            "gpsPerturbated" to Constants.GPS_PERTURBATOR,
            "perturbatorDecimals" to Constants.GPS_PERTURBATOR_DECIMALS,
            "dummyUpdatesCount" to Constants.DUMMY_UPDATES_COUNT,
            "dummyUpdatesRadiusMin" to Constants.DUMMY_MIN_RADIUS,
            "dummyUpdatesRadiusMax" to Constants.DUMMY_MAX_RADIUS
        )
    }
}