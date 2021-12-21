package it.unibo.giacche.contextaware.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.EditText
import android.widget.SeekBar
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import dagger.hilt.android.AndroidEntryPoint
import it.unibo.giacche.contextaware.R
import it.unibo.giacche.contextaware.communication.CanReceiveId
import it.unibo.giacche.contextaware.models.Status
import it.unibo.giacche.contextaware.services.TrackingService
import it.unibo.giacche.contextaware.utils.Constants
import it.unibo.giacche.contextaware.utils.Constants.ACTION_DONT_SEND_LOCATIONS
import it.unibo.giacche.contextaware.utils.Constants.ACTION_DO_SEND_LOCATIONS
import it.unibo.giacche.contextaware.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import it.unibo.giacche.contextaware.utils.Constants.ACTION_STOP_SERVICE
import it.unibo.giacche.contextaware.utils.Constants.SHARED_PREFS_NAME
import it.unibo.giacche.contextaware.utils.PermissionsManager
import it.unibo.giacche.contextaware.utils.PrivacyConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject
import kotlin.math.round


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    @Inject
    lateinit var idReceiver: CanReceiveId

    private lateinit var serviceActivationSw: SwitchCompat
    private lateinit var dummyUpdatesSw: SwitchCompat
    private lateinit var gpsPerturbationSw: SwitchCompat
    private lateinit var sendLocationsSw: SwitchCompat
    private lateinit var spatialCloakingSw: SwitchCompat
    private lateinit var useAlphaSw: SwitchCompat
    private lateinit var noiseTxt: TextView
    private lateinit var collectedFeaturesTxt: TextView
    private lateinit var collectedNoiseTxt: TextView
    private lateinit var alphaValueTxt: TextView
    private lateinit var alphaValueSkBar: SeekBar
    private lateinit var spatCloakTimeout: EditText
    private lateinit var spatCloakRangeX: EditText
    private lateinit var spatCloakRangeY: EditText
    private lateinit var spatCloakK: EditText
    private lateinit var dumMin: EditText
    private lateinit var dumRange: EditText
    private lateinit var dumCount: EditText
    private lateinit var realDecimals: EditText

    private lateinit var progressBar: ProgressBar

    private fun bindElements() {
        serviceActivationSw = findViewById(R.id.service_toggle)
        gpsPerturbationSw = findViewById(R.id.gps_perturbation)
        dummyUpdatesSw = findViewById(R.id.dummy_updates)
        sendLocationsSw = findViewById(R.id.send_locations_toggle)
        spatialCloakingSw = findViewById(R.id.spatial_cloaking)
        useAlphaSw = findViewById(R.id.use_alpha)
        noiseTxt = findViewById(R.id.noiseTextView)
        collectedNoiseTxt = findViewById(R.id.collectedNoiseTextView)
        collectedFeaturesTxt = findViewById(R.id.collectedFeaturesTextView)
        alphaValueSkBar = findViewById(R.id.alphaValue)
        alphaValueTxt = findViewById(R.id.alphaValueText)
        spatCloakTimeout = findViewById(R.id.spatCloakTimeout)
        spatCloakRangeX = findViewById(R.id.spatCloakRangeX)
        spatCloakRangeY = findViewById(R.id.spatCloakRangeY)
        spatCloakK = findViewById(R.id.spatialK)
        dumMin = findViewById(R.id.dumMin)
        dumRange = findViewById(R.id.dumRange)
        dumCount = findViewById(R.id.dumCount)
        realDecimals = findViewById(R.id.realDecimals)
        progressBar = findViewById(R.id.loadingProgressBar)
    }

    private fun setDefaultValues() {
        useAlphaSw.isChecked = Constants.DEFAULT_ALPHA_ENABLED
        alphaValueSkBar.isEnabled = Constants.DEFAULT_ALPHA_ENABLED
        gpsPerturbationSw.isChecked = Constants.DEFAULT_DUMMY_UPDATES_ENABLED
        dummyUpdatesSw.isChecked = Constants.DEFAULT_DUMMY_UPDATES_ENABLED
        spatialCloakingSw.isChecked = Constants.DEFAULT_SPATIAL_CLOAKING_ENABLED
        realDecimals.setText(Constants.DEFAULT_GPS_PERT_REAL_DECIMALS.toString())
        dumMin.setText(Constants.DEFAULT_D_UP_MIN.toString())
        dumRange.setText(Constants.DEFAULT_D_UP_RANGE.toString())
        dumCount.setText(Constants.DEFAULT_D_UP_COUNT.toString())
        spatCloakTimeout.setText((Constants.DEFAULT_SP_CL_TIMEOUT / 1000.0).toString())
        spatCloakK.setText(Constants.DEFAULT_SP_CL_K.toString())
        spatCloakRangeX.setText(Constants.DEFAULT_SP_CL_RANGE.toString())
        spatCloakRangeY.setText(Constants.DEFAULT_SP_CL_RANGE.toString())
        alphaValueSkBar.progress = (Constants.DEFAULT_ALPHA_VALUE * alphaValueSkBar.max).toInt()
    }

    @SuppressLint("SetTextI18n", "ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionsManager.requestPermissions(this)
        setContentView(R.layout.activity_main)

        val prefs = this.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

        GlobalScope.launch(Dispatchers.Main) {
            if(!prefs.contains(Constants.REQ_ID_KEY))
                prefs.edit().putString(Constants.REQ_ID_KEY, idReceiver.getId()).commit()

            PrivacyConfiguration.REQ_ID = prefs.getString(Constants.REQ_ID_KEY, "")!!
            progressBar.visibility = View.INVISIBLE
        }

        bindElements()

        TrackingService.averageNoise.observe(this, {
            when (it.status) {
                Status.ERROR -> noiseTxt.text = "Error: " + it.message
                Status.SUCCESS -> noiseTxt.text = it.data.toString()
                Status.LOADING -> noiseTxt.text = "Loading: " + it.message
            }
        })

        TrackingService.lastNoiseLevel.observe(this, {
            collectedNoiseTxt.text = if (it != null) "$it\r\n${collectedNoiseTxt.text}"
            else ""
        })

        TrackingService.collectedFeaturesMessage.observe(this, {
            collectedFeaturesTxt.text = it
        })

        checkLocationTrackerAvailability()

        serviceActivationSw.setOnCheckedChangeListener { _, isChecked ->
            gpsPerturbationSw.isEnabled = !isChecked
            dummyUpdatesSw.isEnabled = !isChecked
            spatialCloakingSw.isEnabled = !isChecked
            useAlphaSw.isEnabled = !isChecked
            PrivacyConfiguration.GPS_PERT_ENABLED = gpsPerturbationSw.isChecked
            PrivacyConfiguration.DUMMY_UPDATES_ENABLED = dummyUpdatesSw.isChecked
            PrivacyConfiguration.SPATIAL_CLOAKING_ENABLED = spatialCloakingSw.isChecked
            PrivacyConfiguration.ALPHA_ENABLED = useAlphaSw.isChecked
            PrivacyConfiguration.GPS_PERT_REAL_DECIMALS = realDecimals.text.toString().toInt()
            PrivacyConfiguration.D_UP_MIN = dumMin.text.toString().toDouble()
            PrivacyConfiguration.D_UP_RANGE = dumRange.text.toString().toDouble()
            PrivacyConfiguration.D_UP_COUNT = dumCount.text.toString().toInt()
            PrivacyConfiguration.SP_CL_TIMEOUT = round(spatCloakTimeout.text.toString().toDouble() * 1000).toInt()
            PrivacyConfiguration.SP_CL_RANGEX = spatCloakRangeX.text.toString().toDouble()
            PrivacyConfiguration.SP_CL_RANGEY = spatCloakRangeY.text.toString().toDouble()
            PrivacyConfiguration.SP_CL_K = spatCloakK.text.toString().toInt()
            PrivacyConfiguration.ALPHA_VALUE = alphaValueTxt.text.toString().toDouble()
            sendCommandToService(if (sendLocationsSw.isChecked) ACTION_DO_SEND_LOCATIONS else ACTION_DONT_SEND_LOCATIONS )
            sendCommandToService(if (isChecked) ACTION_START_OR_RESUME_SERVICE else ACTION_STOP_SERVICE)
        }

        sendLocationsSw.setOnCheckedChangeListener { _, isChecked ->
            sendCommandToService(if (isChecked) ACTION_DO_SEND_LOCATIONS else ACTION_DONT_SEND_LOCATIONS)
        }

        gpsPerturbationSw.setOnCheckedChangeListener { _, isChecked ->
            realDecimals.isEnabled = isChecked
            if(isChecked) useAlphaSw.isChecked = false
        }

        dummyUpdatesSw.setOnCheckedChangeListener { _, isChecked ->
            dumMin.isEnabled = isChecked
            dumRange.isEnabled = isChecked
            dumCount.isEnabled = isChecked
            if(isChecked) useAlphaSw.isChecked = false
        }

        spatialCloakingSw.setOnCheckedChangeListener { _, isChecked ->
            spatCloakTimeout.isEnabled = isChecked
            spatCloakRangeX.isEnabled = isChecked
            spatCloakRangeY.isEnabled = isChecked
            spatCloakK.isEnabled = isChecked
            if(isChecked) useAlphaSw.isChecked = false
        }

        useAlphaSw.setOnCheckedChangeListener { _, isChecked ->
            alphaValueSkBar.isEnabled = isChecked
            if(isChecked) {
                gpsPerturbationSw.isChecked = false
                dummyUpdatesSw.isChecked = false
                spatialCloakingSw.isChecked = false
            }
        }

        alphaValueSkBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                alphaValueTxt.text = "${i.toDouble() / seekBar.max}"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        setDefaultValues()
    }

    private fun checkLocationTrackerAvailability() {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            AlertDialog.Builder(this).setMessage(getString(R.string.gps_disabled_message))
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> dialog.cancel() }
                .create()
                .show()
    }

    private fun sendCommandToService(action: String) =
        Intent(this, TrackingService::class.java).also {
            it.action = action
            startService(it)
        }

    // permission callbacks logic

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms))
            AppSettingsDialog.Builder(this).build().show()
        else PermissionsManager.requestPermissions(this)
    }

    // Nothing to do when permission is granted
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}