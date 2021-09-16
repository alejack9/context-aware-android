package it.unibo.giacche.contextaware.location.privacymechanisms

import android.location.Location
import it.unibo.giacche.contextaware.location.CanMakeDummyLocation
import it.unibo.giacche.contextaware.models.ModifiedLocation
import it.unibo.giacche.contextaware.utils.Constants
import it.unibo.giacche.contextaware.utils.Constants.DUMMY_MAX_RADIUS
import it.unibo.giacche.contextaware.utils.Constants.DUMMY_MIN_RADIUS
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object DummyLocationMaker: CanMakeDummyLocation {
    override fun from(location: ModifiedLocation): ModifiedLocation {
        // Random angle from 0 to 3.14
        val theta = Random.nextDouble(2 * Math.PI)
        // Get x and y (sin and cos) change distance from point of a max of DUMMY_MAX_RADIUS
        val offset = arrayOf(sin(theta), cos(theta)).map { x ->
            x * Random.nextDouble(DUMMY_MIN_RADIUS, DUMMY_MAX_RADIUS)
        }
        return ModifiedLocation(Location(location.location).apply {
            // Add or remove offset
            longitude += offset[0] * Random.nextDouble(-1.0,1.0)
            latitude += offset[1] * Random.nextDouble(-1.0,1.0)
        }, true, location.gpsPerturbated)

    }
}