package it.unibo.giacche.contextaware.communication.senders

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.giacche.contextaware.communication.CanSendLocation
import it.unibo.giacche.contextaware.utils.Constants
import kotlinx.coroutines.delay
import org.geojson.FeatureCollection
import timber.log.Timber


class LocationSenderMockup : CanSendLocation {
    private val objMapper = ObjectMapper()

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun send(features: FeatureCollection) {
        val serialized = objMapper.writeValueAsString(features)
        Timber.d(
            "Sending to ${Constants.DESTINATION_URL}:\r\n$serialized..."
        )
        delay(1000)
        Timber.d("... Sent")
    }
}
