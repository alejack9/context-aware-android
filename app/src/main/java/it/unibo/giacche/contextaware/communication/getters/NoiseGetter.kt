package it.unibo.giacche.contextaware.communication.getters

import android.location.Location
import it.unibo.giacche.contextaware.communication.CanMakeDummyLocation
import it.unibo.giacche.contextaware.communication.CanMakeGpsPerturbation
import it.unibo.giacche.contextaware.communication.CanReceiveNoise
import it.unibo.giacche.contextaware.communication.OkHttpClientWrapper
import it.unibo.giacche.contextaware.communication.privacymechanisms.DummyLocationMaker
import it.unibo.giacche.contextaware.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.random.Random
import java.util.*

class NoiseGetter(private val client: OkHttpClientWrapper) : CanReceiveNoise {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun getNoise(
        location: Location
    ): Double? = client.getNoise(location)
//        = withContext(Dispatchers.IO) {
//        val correct = Random.nextInt(0, Constants.DUMMY_UPDATES)
//
//        val locations = (0..Constants.DUMMY_UPDATES).map { i ->
//            if (i != correct) dummyLocationMaker.from(location) else location
//        }
//        val req = createRequest(locations)
//
//        return@withContext client.newCall(req)
//            .execute().use { res ->
//                if (!res.isSuccessful) throw Error("Backend error: ${res.code} - ${res.message}")
//                val noisesString = res.body!!.string()
//                noisesString.substring(1, noisesString.length - 1)
//                    .split(",")[correct].toDoubleOrNull()
//            }
//    }

//    private fun createRequest(locations: List<Location>): Request {
//        return Request.Builder().url(
//            Constants.DESTINATION_URL.toHttpUrlOrNull()!!.newBuilder()
//                .addQueryParameter(
//                    "lats",
//                    locations.joinToString(",") { location -> location.latitude.toString() }
//                )
//                .addQueryParameter(
//                    "longs",
//                    locations.joinToString(",") { location -> location.longitude.toString() }
//                ).build().toString()
//        ).build()
//    }
}