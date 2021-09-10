package it.unibo.giacche.contextaware.location

import android.location.Location

interface CanMakeDummyLocation {
    fun from(location: Location): Location
}