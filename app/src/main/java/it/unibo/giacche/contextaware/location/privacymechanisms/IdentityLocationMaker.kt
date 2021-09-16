package it.unibo.giacche.contextaware.location.privacymechanisms

import it.unibo.giacche.contextaware.location.CanMakeDummyLocation
import it.unibo.giacche.contextaware.location.CanMakeGpsPerturbation
import it.unibo.giacche.contextaware.models.ModifiedLocation

object IdentityLocationMaker: CanMakeDummyLocation, CanMakeGpsPerturbation {
    override fun from(location: ModifiedLocation): ModifiedLocation = location
}