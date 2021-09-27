package it.unibo.giacche.contextaware.communication

import android.location.Location

interface CanReceiveNoise {
    suspend fun getNoise(
        location: Location
    ): Double?

    fun enableDummyUpdates(
        enable: Boolean
    )

    fun enableGpsPerturbator(
        enable: Boolean
    )
}