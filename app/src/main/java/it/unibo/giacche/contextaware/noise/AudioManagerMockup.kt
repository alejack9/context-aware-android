package it.unibo.giacche.contextaware.noise

import kotlin.random.Random

class AudioManagerMockup : CanReturnNoise {
    override fun getNoise(callback: (Double) -> Unit) {
        callback(Random.nextDouble(-0.09,0.00005))
    }

}