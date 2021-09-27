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
        private const val suffix = "trusted"
        private var dummyUpdatesEnabled = false
        private var perturbatorEnabled = false
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

    override fun enableDummyUpdates(enable: Boolean) {
        dummyUpdatesEnabled = enable
    }

    override fun enableGpsPerturbator(enable: Boolean) {
        perturbatorEnabled = enable
    }

    private fun createRequest(location: Location): Request {
        return Request.Builder().url(
            (Constants.DESTINATION_URL + suffix).toHttpUrlOrNull()!!.newBuilder()
                .addQueryParameter(
                    "lat",
                    location.latitude.toString()
                )
                .addQueryParameter(
                    "long",
                    location.longitude.toString()
                )
                .addQueryParameter(
                    "perturbatorEnabled", perturbatorEnabled.toString()
                )
                .addQueryParameter(
                    "dummyUpdatesEnabled", dummyUpdatesEnabled.toString()
                )
                .build().toString()
        ).build()
    }

}