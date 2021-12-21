package it.unibo.giacche.contextaware.communication

import android.location.Location

interface CanReceiveId {
    suspend fun getId(): String
}