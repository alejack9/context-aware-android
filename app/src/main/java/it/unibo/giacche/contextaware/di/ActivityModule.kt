package it.unibo.giacche.contextaware.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import it.unibo.giacche.contextaware.communication.getters.IdGetter
import it.unibo.giacche.contextaware.communication.getters.IdGetterMockup

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    @Provides
    fun provideIdGetter(
    ) = IdGetter()
    @Provides
    fun provideIdGetterMockup(
    ) = IdGetterMockup()
}
