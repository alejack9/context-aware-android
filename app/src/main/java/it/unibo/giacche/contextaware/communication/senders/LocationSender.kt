package it.unibo.giacche.contextaware.communication.senders

import it.unibo.giacche.contextaware.communication.CanSendLocation
import it.unibo.giacche.contextaware.communication.OkHttpClientWrapper
import org.geojson.FeatureCollection

class LocationSender(private val client: OkHttpClientWrapper) : CanSendLocation {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun send(locations: FeatureCollection) = client.send(locations)
}