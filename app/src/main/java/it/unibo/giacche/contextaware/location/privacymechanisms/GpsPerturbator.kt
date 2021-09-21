package it.unibo.giacche.contextaware.location.privacymechanisms

import android.location.Location
import it.unibo.giacche.contextaware.location.CanMakeGpsPerturbation
import it.unibo.giacche.contextaware.models.ModifiedLocation
import it.unibo.giacche.contextaware.utils.Constants.GPS_PERTURBATOR_DECIMALS
import kotlin.math.pow
import kotlin.math.round

object GpsPerturbator : CanMakeGpsPerturbation {
    override fun from(location: ModifiedLocation): ModifiedLocation {
        return ModifiedLocation(Location(location.location).apply {
            this.longitude = round(longitude * 10.0.pow(GPS_PERTURBATOR_DECIMALS)) / 10.0.pow(GPS_PERTURBATOR_DECIMALS);
            this.latitude = round(latitude * 10.0.pow(GPS_PERTURBATOR_DECIMALS)) / 10.0.pow(GPS_PERTURBATOR_DECIMALS);
        }, location.dummyLocation, true)
    }
}