package it.unibo.giacche.contextaware.communication.getters

import android.location.Location
import it.unibo.giacche.contextaware.communication.CanReceiveNoise
import it.unibo.giacche.contextaware.communication.OkHttpClientWrapper

class NoiseGetter(private val client: OkHttpClientWrapper) : CanReceiveNoise {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getNoise(
        location: Location
    ): Double? = client.getNoise(location)
}