package it.unibo.giacche.contextaware.communication.privacymechanisms

import android.location.Location
import it.unibo.giacche.contextaware.communication.CanMakeDummyLocation
import it.unibo.giacche.contextaware.communication.CanMakeGpsPerturbation
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object IdentityLocationMaker: CanMakeDummyLocation, CanMakeGpsPerturbation {
    override fun from(location: Location): Location {
        return location
    }
}