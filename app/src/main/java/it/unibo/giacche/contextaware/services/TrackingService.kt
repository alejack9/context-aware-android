package it.unibo.giacche.contextaware.services

import android.content.Intent
import android.location.Location
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.AndroidEntryPoint
import it.unibo.giacche.contextaware.communication.*
import it.unibo.giacche.contextaware.models.FeatureFactory
import it.unibo.giacche.contextaware.noise.CanReturnNoise
import it.unibo.giacche.contextaware.communication.privacymechanisms.DummyLocationMaker
import it.unibo.giacche.contextaware.communication.privacymechanisms.IdentityLocationMaker
import it.unibo.giacche.contextaware.location.LocationController
import it.unibo.giacche.contextaware.models.Resource
import it.unibo.giacche.contextaware.models.Status
import it.unibo.giacche.contextaware.utils.Constants
import it.unibo.giacche.contextaware.utils.Constants.ACTION_DISABLE_DUMMY_UPDATES
import it.unibo.giacche.contextaware.utils.Constants.ACTION_DISABLE_GPS_PERTURBATION
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
    lateinit var sender: CanSendLocation

    @Inject
    lateinit var getter: CanReceiveNoise

    @Inject
    lateinit var audioManager: CanReturnNoise

    private val locationHandler = fun(location: Location) {
        audioManager.getNoise { noise ->
            recorded.last().add(FeatureFactory.build(location, noise))

            if (recorded.peek()!!.features.size >= Constants.RECORDED_LOCATION_BUFFER_SIZE)
                sendData()
            else
                notificationsManager.setText(
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
            if (res != null) Resource(Status.SUCCESS, res, null)
            else Resource(Status.ERROR, null, "No information here!")
        } catch (e: Error) {
           Resource(Status.ERROR, null, e.message)
       }catch (e: Exception) {
           Resource(Status.ERROR, null, e.message)
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
        GlobalScope.launch(Dispatchers.Main) {
            try {
                sender.send(toInsert)
                notificationsManager.setText(
                    "Last Sent: ${
                        SimpleDateFormat("HH:mm:ss dd/MM", Locale.ITALY).format(Date())
                    }"
                )
            } catch (e: IOException) {
                discarded.add(toInsert)
                Timber.e(e)
            }
        }
        recorded.add(FeatureCollection())
    }

    companion object {
        val isActive = MutableLiveData<Boolean>()
        val averageNoise = MutableLiveData<Resource<Double>>()
        private var isKilled = false
        private var sendingDiscarded = false
        private val recorded: Queue<FeatureCollection> = LinkedList<FeatureCollection>().apply {
            this.add(FeatureCollection())
        }
        private val discarded: Queue<FeatureCollection> = LinkedList()
    }

    override fun onCreate() {
        super.onCreate()
        isActive.postValue(true)

        notificationsManager.startForegroundService(this)

        isActive.observe(this, {
            locationController.updateLocationTracking(it, locationHandler)
            if (!isKilled)
                notificationsManager.viewNotification()
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
                    OkHttpClientWrapper.dummyLocationMaker = DummyLocationMaker
                    Timber.d("Dummy updates enabled")
                }
                ACTION_DISABLE_DUMMY_UPDATES -> {
                    OkHttpClientWrapper.dummyLocationMaker = IdentityLocationMaker
                    Timber.d("Dummy updates disabled")
                }
                ACTION_ENABLE_GPS_PERTURBATION -> {
                    // TODO
//                    OkHttpClientWrapper.gpsPerturbatorMaker = DummyLocationMaker
                    Timber.d("Dummy updates enabled")
                }
                ACTION_DISABLE_GPS_PERTURBATION -> {
                    OkHttpClientWrapper.dummyLocationMaker = IdentityLocationMaker
                    Timber.d("Dummy updates disabled")
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
