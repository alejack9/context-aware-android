package it.unibo.giacche.contextaware.communication

import android.location.Location

interface CanMakeGpsPerturbation {
    fun from(location: Location): Location
}
