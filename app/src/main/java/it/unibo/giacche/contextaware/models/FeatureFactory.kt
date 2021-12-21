package it.unibo.giacche.contextaware.models

import android.location.Location
import org.geojson.Feature
import org.geojson.Point
import it.unibo.giacche.contextaware.utils.Constants
import it.unibo.giacche.contextaware.utils.PrivacyConfiguration

object FeatureFactory {
    var n = 0
    fun build(location: Location, noise: Double) = Feature().apply {
        geometry = Point(location.longitude, location.latitude)
        properties = GeoJSONProperties(
            reqId = PrivacyConfiguration.REQ_ID,
            reqNr = n++,
            timeStamp = location.time,
            noiseLevel = noise,
            alpha = PrivacyConfiguration.ALPHA_ENABLED,
            alphaValue = PrivacyConfiguration.ALPHA_VALUE,
            cloaking = PrivacyConfiguration.SPATIAL_CLOAKING_ENABLED,
            cloakingTimeout = PrivacyConfiguration.SP_CL_TIMEOUT,
            cloakingK = PrivacyConfiguration.SP_CL_K,
            cloakingSizeX = PrivacyConfiguration.SP_CL_RANGEX,
            cloakingSizeY = PrivacyConfiguration.SP_CL_RANGEY,
            dummyLocation = PrivacyConfiguration.DUMMY_UPDATES_ENABLED,
            dummyUpdatesCount = PrivacyConfiguration.D_UP_COUNT,
            dummyUpdatesRadiusMin = PrivacyConfiguration.D_UP_MIN,
            dummyUpdatesRadiusStep = PrivacyConfiguration.D_UP_RANGE,
            gpsPerturbated = PrivacyConfiguration.GPS_PERT_ENABLED,
            perturbatorDecimals = PrivacyConfiguration.GPS_PERT_REAL_DECIMALS
        ).toMap()
    }
}