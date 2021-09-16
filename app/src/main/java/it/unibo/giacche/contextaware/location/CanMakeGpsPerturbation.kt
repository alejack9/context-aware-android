package it.unibo.giacche.contextaware.location

import it.unibo.giacche.contextaware.models.ModifiedLocation

interface CanMakeGpsPerturbation {
    fun from(location: ModifiedLocation): ModifiedLocation
}
