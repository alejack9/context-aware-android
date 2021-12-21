package it.unibo.giacche.contextaware.communication.getters

import android.location.Location
import it.unibo.giacche.contextaware.communication.CanReceiveNoise

class NoiseGetterMockup: CanReceiveNoise {
    override suspend fun getNoise(location: Location): Double? {
        return 3.0
    }
}