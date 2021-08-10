package it.unibo.giacche.contextaware.communication

import android.location.Location
import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.giacche.contextaware.communication.privacymechanisms.IdentityLocationMaker
import it.unibo.giacche.contextaware.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.geojson.FeatureCollection
import kotlin.random.Random

class OkHttpClientWrapper{
    companion object {
        private val client = OkHttpClient()
        var dummyLocationMaker: CanMakeDummyLocation = IdentityLocationMaker
        var gpsPerturbatorMaker: CanMakeGpsPerturbation = IdentityLocationMaker
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun send(locations: FeatureCollection) = withContext(Dispatchers.IO) {
        // TODO DUMMY LOCATION MAKER
        val request = Request.Builder().url(Constants.DESTINATION_URL)
            .post(
                ObjectMapper().writeValueAsString(locations)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()!!)
            )
            .build()
        var sent: Boolean
        do {
            sent = client.newCall(request).execute().isSuccessful
            if (!sent) delay(Constants.SEND_RETRY_TIMEOUT)
        } while (!sent)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getNoise(location: Location): Double? =
        withContext(Dispatchers.IO) {
            val correct = Random.nextInt(0, Constants.DUMMY_UPDATES)

            val locations = (0..Constants.DUMMY_UPDATES).map { i ->
                if (i != correct) dummyLocationMaker.from(location) else location
            }
            val req = createRequest(locations)

            return@withContext client.newCall(req)
                .execute().use { res ->
                    if (!res.isSuccessful) throw Error("Backend error: ${res.code} - ${res.message}")
                    val noisesString = res.body!!.string()
                    noisesString.substring(1, noisesString.length - 1)
                        .split(",")[correct].toDoubleOrNull()
                }
        }

    private fun createRequest(locations: List<Location>): Request {
        return Request.Builder().url(
            Constants.DESTINATION_URL.toHttpUrlOrNull()!!.newBuilder()
                .addQueryParameter(
                    "lats",
                    locations.joinToString(",") { location -> location.latitude.toString() }
                )
                .addQueryParameter(
                    "longs",
                    locations.joinToString(",") { location -> location.longitude.toString() }
                ).build().toString()
        ).build()
    }
}