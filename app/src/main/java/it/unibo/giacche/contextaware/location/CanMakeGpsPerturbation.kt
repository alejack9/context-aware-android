package it.unibo.giacche.contextaware.location

import android.location.Location

interface CanMakeGpsPerturbation {
    fun from(location: Location): Location
}
