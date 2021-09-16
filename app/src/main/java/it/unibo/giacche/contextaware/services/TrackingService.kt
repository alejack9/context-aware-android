package it.unibo.giacche.contextaware.services

import android.content.Intent
import android.location.Location
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.AndroidEntryPoint
import it.unibo.giacche.contextaware.communication.*
import it.unibo.giacche.contextaware.communication.senders.LocationSenderMockup
import it.unibo.giacche.contextaware.models.FeatureFactory
import it.unibo.giacche.contextaware.noise.CanReturnNoise
import it.unibo.giacche.contextaware.location.privacymechanisms.DummyLocationMaker
import it.unibo.giacche.contextaware.location.privacymechanisms.IdentityLocationMaker
import it.unibo.giacche.contextaware.location.LocationController
import it.unibo.giacche.contextaware.location.privacymechanisms.GpsPerturbator
import it.unibo.giacche.contextaware.models.Resource
import it.unibo.giacche.contextaware.utils.Constants
import it.unibo.giacche.contextaware.utils.Constants.ACTION_DISABLE_DUMMY_UPDATES
import it.unibo.giacche.contextaware.utils.Constants.ACTION_DISABLE_GPS_PERTURBATION
import it.unibo.giacche.contextaware.utils.Constants.ACTION_DONT_SEND_LOCATIONS
import it.unibo.giacche.contextaware.utils.Constants.ACTION_DO_SEND_LOCATIONS
import it.unibo.giacche.contextaware.utils.Constants.ACTION_ENABLE_DUMMY_UPDATES
import it.unibo.giacche.contextaware.utils.Constants.ACTION_ENABLE_GPS_PERTURBATION
import it.unibo.giacche.contextaware.utils.Constants.ACTION_PAUSE_SERVICE
import it.unibo.giacche.contextaware.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import it.unibo.giacche.contextaware.utils.Constants.ACTION_STOP_SERVICE
import it.unibo.giacche.contextaware.utils.NotificationsManager
import kotlinx.coroutines.*
import org.geojson.FeatureCollection
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class TrackingService : LifecycleService() {

    @Inject
    lateinit var notificationsManager: NotificationsManager

    @Inject
    lateinit var locationController: LocationController

    @Inject
    lateinit var defaultSender: CanSendLocation

    @Inject
    lateinit var getter: CanReceiveNoise

    @Inject
    lateinit var audioManager: CanReturnNoise

    private lateinit var sender: CanSendLocation

    companion object {
        val isActive = MutableLiveData<Boolean>()
        val averageNoise = MutableLiveData<Resource<Double>>()
        private var isKilled = false
        private var sendingDiscarded = false
        private val recorded: Queue<FeatureCollection> = LinkedList<FeatureCollection>().apply {
            this.add(FeatureCollection())
        }
        val collectedFeaturesMessage = MutableLiveData("")
        val lastNoiseLevel = MutableLiveData<Double?>()
        private val discarded: Queue<FeatureCollection> = LinkedList()
    }

    private val locationHandler = fun(location: Location) {
        if (!isActive.value!!) return
        audioManager.getNoise { noise ->
            recorded.last().add(FeatureFactory.build(location, noise))
            lastNoiseLevel.postValue(noise)

            if (recorded.peek()!!.features.size >= Constants.RECORDED_LOCATION_BUFFER_SIZE)
                sendData()
            else
                collectedFeaturesMessage.postValue(
                    "${recorded.peek()!!.features.size} / ${
                        Constants.RECORDED_LOCATION_BUFFER_SIZE
                    }"
                )
        }

        getAverageNoise(location)
        sendDiscarded()
    }

    private fun getAverageNoise(location: Location) = GlobalScope.launch(Dispatchers.Main) {
        val resource = try {
            val res = getter.getNoise(location)
            if (res != null) Resource.success(res)
            else Resource.error("No information here!")
        } catch (e: Error) {
            Resource.error(e.message ?: "Unknown error")
        } catch (e: Exception) {
            Resource.error(e.message ?: "Unknown error")
        }
        averageNoise.postValue(resource)
    }

    private fun sendDiscarded() = GlobalScope.launch(Dispatchers.Main) {
        if (sendingDiscarded) return@launch
        sendingDiscarded = true
        while (!discarded.isEmpty()) {
            val toSend = discarded.remove()
            try {
                sender.send(toSend)
            } catch (e: IOException) {
                discarded.add(toSend)
                Timber.e(e)
                delay(Constants.SEND_RETRY_TIMEOUT)
            }
        }
        sendingDiscarded = false
    }

    private fun sendData() {
        val toInsert = recorded.remove()
        lastNoiseLevel.postValue(null)
        GlobalScope.launch(Dispatchers.Main) {
            try {
                sender.send(toInsert)
                collectedFeaturesMessage.postValue(
                    "Last Sent: ${
                        SimpleDateFormat(
                            "HH:mm:ss dd/MM",
                            Locale.ITALY
                        ).format(Date())
                    }"
                )
            } catch (e: IOException) {
                discarded.add(toInsert)
                Timber.e(e)
            }
        }
        recorded.add(FeatureCollection())
    }

    override fun onCreate() {
        super.onCreate()
        isActive.postValue(false)

        notificationsManager.startForegroundService(this)

        isActive.observe(this, {
            locationController.updateLocationTracking(it, locationHandler)
            if (!isKilled)
                notificationsManager.viewNotification()
        })

        collectedFeaturesMessage.observe(this, {
            notificationsManager.setText(it)
        })
    }

    // Called when we send an intent to the service
// three possible actions to send: resume/pause/stop
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    isActive.postValue(true)
                    if (isKilled) {
                        isKilled = false
                        notificationsManager.startForegroundService(this)
                    }
                    Timber.d("Service Started or Resumed")
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Timber.d("Service Paused")
                }
                ACTION_STOP_SERVICE -> {
                    killService()
                    Timber.d("Service Stopped")
                }
                ACTION_ENABLE_DUMMY_UPDATES -> {
                    getter.setDummyUpdateMechanism(DummyLocationMaker)
                    Timber.d("Dummy updates enabled")
                }
                ACTION_DISABLE_DUMMY_UPDATES -> {
                    getter.setDummyUpdateMechanism(IdentityLocationMaker)
                    Timber.d("Dummy updates disabled")
                }
                ACTION_ENABLE_GPS_PERTURBATION -> {
                    getter.setGpsPerturbator(GpsPerturbator)
                    Timber.d("Gps Perturbator enabled")
                }
                ACTION_DISABLE_GPS_PERTURBATION -> {
                    getter.setGpsPerturbator(IdentityLocationMaker)
                    Timber.d("Gps Perturbator disabled")
                }
                ACTION_DO_SEND_LOCATIONS -> {
                    Timber.d("Setting sender to default")
                    sender = defaultSender
                }
                ACTION_DONT_SEND_LOCATIONS -> {
                    Timber.d("Setting sender to mockup")
                    sender = LocationSenderMockup()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService() {
        sendData()
        isActive.postValue(false)
    }

    private fun killService() {
        isKilled = true
        pauseService()
        stopForeground(true)
        stopSelf()
    }
}
