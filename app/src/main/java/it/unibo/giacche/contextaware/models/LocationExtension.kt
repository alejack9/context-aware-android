package it.unibo.giacche.contextaware.models

import android.location.Location

fun Location.toModifiedLocation() = ModifiedLocation(this,
    dummyLocation = false,
    gpsPerturbated = false
)