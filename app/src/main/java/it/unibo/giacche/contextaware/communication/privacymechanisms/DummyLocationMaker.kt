package it.unibo.giacche.contextaware.communication.privacymechanisms

import android.location.Location
import it.unibo.giacche.contextaware.communication.CanMakeDummyLocation
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object DummyLocationMaker: CanMakeDummyLocation {
    override fun from(location: Location): Location {
        val theta = Math.random() * 2 * Math.PI
        val offset = arrayOf(sin(theta), cos(theta)).map { x ->
            x * (Math.random() * 0.002)
        }
        return Location(location).apply {
            longitude += offset[0] + Random.nextDouble(-1.0,1.0)
            latitude += offset[1] + Random.nextDouble(-1.0,1.0)
        }
    }
}