package it.unibo.giacche.contextaware.communication.getters

import it.unibo.giacche.contextaware.communication.CanReceiveId
import it.unibo.giacche.contextaware.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class IdGetter : CanReceiveId {
    companion object {
        private val client = OkHttpClient()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getId(): String = withContext(Dispatchers.IO) {
        return@withContext client.newCall(Request.Builder().url(Constants.ID_ENDPOINT).build())
            .execute().use { res ->
                if (!res.isSuccessful) throw Error("Backend error: ${res.code} - ${res.message}")
                res.body!!.string()
            }
    }
}