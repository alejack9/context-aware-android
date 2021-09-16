package it.unibo.giacche.contextaware.communication.getters

import android.location.Location
import it.unibo.giacche.contextaware.communication.CanReceiveNoise
import it.unibo.giacche.contextaware.location.CanMakeDummyLocation
import it.unibo.giacche.contextaware.location.CanMakeGpsPerturbation
import it.unibo.giacche.contextaware.location.privacymechanisms.IdentityLocationMaker
import it.unibo.giacche.contextaware.models.ModifiedLocation
import it.unibo.giacche.contextaware.models.toModifiedLocation
import it.unibo.giacche.contextaware.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import kotlin.random.Random

class NoiseGetter : CanReceiveNoise {
    companion object {
        private val client = OkHttpClient()
        private var dummyLocationMaker: CanMakeDummyLocation = IdentityLocationMaker
        private var gpsPerturbator: CanMakeGpsPerturbation = IdentityLocationMaker
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getNoise(
        location: Location
    ): Double? = withContext(Dispatchers.IO) {
        val correct = Random.nextInt(0, Constants.DUMMY_UPDATES)
        val locations = (0..Constants.DUMMY_UPDATES).map {
            if (it != correct)
                dummyLocationMaker.from(location.toModifiedLocation())
            else location.toModifiedLocation()
        }

        return@withContext client.newCall(createRequest(locations))
            .execute().use { res ->
                if (!res.isSuccessful) throw Error("Backend error: ${res.code} - ${res.message}")
                val noisesString = res.body!!.string()
                noisesString.substring(1, noisesString.length - 1)
                    .split(",")[correct].toDoubleOrNull()
            }
    }

    override fun setDummyUpdateMechanism(mechanism: CanMakeDummyLocation) {
        dummyLocationMaker = mechanism
    }

    override fun setGpsPerturbator(mechanism: CanMakeGpsPerturbation) {
        gpsPerturbator = mechanism
    }

    private fun createRequest(locations: List<ModifiedLocation>): Request {
        return Request.Builder().url(
            Constants.DESTINATION_URL.toHttpUrlOrNull()!!.newBuilder()
                .addQueryParameter(
                    "requests",
                    "{ \"positions\": [%s]}".format(
                        locations.joinToString(",") { location ->
                            "{%s}".format(
                                "\"dummyLocation\": ${
                                    location.dummyLocation
                                }, \"gpsPerturbated\": ${
                                    location.gpsPerturbated
                                }, \"coords\": [${
                                    location.location.longitude
                                }, ${
                                    location.location.latitude
                                }]"
                            )
                        }
                    )
                )
                .build().toString()
        ).build()
    }
}