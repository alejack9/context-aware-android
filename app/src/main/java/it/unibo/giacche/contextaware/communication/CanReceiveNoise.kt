package it.unibo.giacche.contextaware.communication

import android.location.Location
import it.unibo.giacche.contextaware.location.CanMakeDummyLocation
import it.unibo.giacche.contextaware.location.CanMakeGpsPerturbation

interface CanReceiveNoise {
    suspend fun getNoise(
        location: Location
    ): Double?

    fun setDummyUpdateMechanism(
        mechanism: CanMakeDummyLocation
    )

    fun setGpsPerturbator(
        mechanism: CanMakeGpsPerturbation
    )
}