package it.unibo.giacche.contextaware.models

import android.location.Location
import org.geojson.Feature
import org.geojson.Point

object FeatureFactory {
    fun build(location : Location, noise: Double) = Feature().apply {
        geometry = Point(location.longitude, location.latitude)
        properties = mapOf(
            "timeStamp" to location.time,
            "noiseLevel" to noise
        )
    }
}