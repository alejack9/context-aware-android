package it.unibo.giacche.contextaware.noise

interface CanReturnNoise {
    fun getNoise(callback: (Double) -> Unit)
}
