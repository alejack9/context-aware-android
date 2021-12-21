package it.unibo.giacche.contextaware.communication.getters

import android.location.Location
import it.unibo.giacche.contextaware.communication.CanReceiveNoise
import it.unibo.giacche.contextaware.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

class NoiseGetter : CanReceiveNoise {
    companion object {
        private val client = OkHttpClient()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getNoise(
        location: Location
    ): Double? = withContext(Dispatchers.IO) {
        return@withContext client.newCall(createRequest(location))
            .execute().use { res ->
                if (!res.isSuccessful) throw Error("Backend error: ${res.code} - ${res.message}")
                res.body!!.string().toDoubleOrNull()
            }
    }

    private fun createRequest(location: Location): Request {
        return Request.Builder().url(
            (Constants.GET_ENDPOINT).toHttpUrlOrNull()!!.newBuilder()
                .addQueryParameter(
                    "lat",
                    location.latitude.toString()
                )
                .addQueryParameter(
                    "long",
                    location.longitude.toString()
                )
                .build().toString()
        ).build()
    }

}