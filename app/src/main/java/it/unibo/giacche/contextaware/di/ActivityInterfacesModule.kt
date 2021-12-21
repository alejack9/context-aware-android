package it.unibo.giacche.contextaware.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import it.unibo.giacche.contextaware.communication.CanReceiveId
import it.unibo.giacche.contextaware.communication.getters.IdGetter
import it.unibo.giacche.contextaware.communication.getters.IdGetterMockup

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityInterfacesModule {

    @Binds
    abstract fun provideIdGetter(
        noiseReceiver : IdGetter
//        noiseReceiver : IdGetterMockup
    ): CanReceiveId
}