package it.unibo.giacche.contextaware.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import it.unibo.giacche.contextaware.communication.CanReceiveNoise
import it.unibo.giacche.contextaware.noise.AudioManager
import it.unibo.giacche.contextaware.noise.CanReturnNoise
import it.unibo.giacche.contextaware.communication.CanSendLocation
import it.unibo.giacche.contextaware.communication.getters.NoiseGetter
import it.unibo.giacche.contextaware.communication.senders.LocationSender

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceInterfacesModule {

    @ServiceScoped
    @Binds
    abstract fun provideLocationSender(
        locationSender : LocationSender
    ): CanSendLocation

    @ServiceScoped
    @Binds
    abstract fun provideNoiseGetter(
        noiseReceiver : NoiseGetter
    ): CanReceiveNoise

    @ServiceScoped
    @Binds
    abstract fun provideAudioManager(
        audioManager: AudioManager
    ): CanReturnNoise
}