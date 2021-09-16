package it.unibo.giacche.contextaware.location.privacymechanisms

import android.location.Location
import it.unibo.giacche.contextaware.location.CanMakeGpsPerturbation
import it.unibo.giacche.contextaware.models.ModifiedLocation
import it.unibo.giacche.contextaware.utils.Constants.GPS_PERTURBATOR_DECIMALS
import kotlin.math.round

object GpsPerturbator : CanMakeGpsPerturbation {
    override fun from(location: ModifiedLocation): ModifiedLocation {
        return ModifiedLocation(Location(location.location).apply {
            round(longitude * GPS_PERTURBATOR_DECIMALS) / GPS_PERTURBATOR_DECIMALS;
            round(latitude * GPS_PERTURBATOR_DECIMALS) / GPS_PERTURBATOR_DECIMALS;
        }, location.dummyLocation, true)
    }
}