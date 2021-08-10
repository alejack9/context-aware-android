package it.unibo.giacche.contextaware.communication

import android.location.Location

interface CanMakeDummyLocation {
    fun from(location: Location): Location
}