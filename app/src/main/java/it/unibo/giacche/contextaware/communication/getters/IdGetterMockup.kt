package it.unibo.giacche.contextaware.communication.getters

import it.unibo.giacche.contextaware.communication.CanReceiveId
import kotlinx.coroutines.delay
import timber.log.Timber

class IdGetterMockup : CanReceiveId {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getId(): String {
        Timber.d("Waiting 8 secs")
        delay(8000)
        Timber.d("Sending A")
        return "A"
    }
}