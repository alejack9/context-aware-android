package it.unibo.giacche.contextaware.communication

import org.geojson.FeatureCollection

interface CanSendLocation {
    suspend fun send(locations: FeatureCollection)
}