package it.unibo.giacche.contextaware.location.privacymechanisms

import android.location.Location
import it.unibo.giacche.contextaware.location.CanMakeDummyLocation
import it.unibo.giacche.contextaware.location.CanMakeGpsPerturbation

object IdentityLocationMaker: CanMakeDummyLocation, CanMakeGpsPerturbation {
    override fun from(location: Location): Location {
        return location
    }
}