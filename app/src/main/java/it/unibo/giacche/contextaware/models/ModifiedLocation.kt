package it.unibo.giacche.contextaware.models

import android.location.Location

data class ModifiedLocation(
    val location: Location,
    val dummyLocation: Boolean,
    val gpsPerturbated: Boolean
)
