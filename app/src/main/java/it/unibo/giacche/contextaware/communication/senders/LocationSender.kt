package it.unibo.giacche.contextaware.communication.senders

import com.fasterxml.jackson.databind.ObjectMapper
import it.unibo.giacche.contextaware.communication.CanSendLocation
import it.unibo.giacche.contextaware.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.wait
import org.geojson.FeatureCollection

class LocationSender : CanSendLocation {
    companion object {
        private val client = OkHttpClient()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun send(features: FeatureCollection) = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(Constants.SEND_ENDPOINT)
            .post(
                ObjectMapper().writeValueAsString(features)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()!!)
            )
            .build()
//        var sent: Boolean
//        do {
//        sent = client.newCall(request).execute().isSuccessful
        client.newCall(request).execute().wait()
//            if (!sent) delay(Constants.SEND_RETRY_TIMEOUT)
//        } while (!sent)
    }
}