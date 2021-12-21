package it.unibo.giacche.contextaware.di

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import it.unibo.giacche.contextaware.R
import it.unibo.giacche.contextaware.communication.getters.NoiseGetter
import it.unibo.giacche.contextaware.communication.getters.NoiseGetterMockup
import it.unibo.giacche.contextaware.location.LocationController
import it.unibo.giacche.contextaware.communication.senders.LocationSender
import it.unibo.giacche.contextaware.noise.AudioManager
import it.unibo.giacche.contextaware.noise.AudioManagerMockup
import it.unibo.giacche.contextaware.communication.senders.LocationSenderMockup
import it.unibo.giacche.contextaware.utils.Constants
import it.unibo.giacche.contextaware.utils.NotificationsManager
import it.unibo.giacche.contextaware.views.MainActivity

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @SuppressLint("VisibleForTests")
    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context
    ) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    )!!

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false) // when the user tap on the notification it doesn't disappear
        .setOngoing(true) // can't be swiped away
        .setSmallIcon(R.drawable.ic_baseline_pin_drop_24)
        .setContentTitle("Context Aware App")
        .setContentIntent(pendingIntent)

    @ServiceScoped
    @Provides
    fun provideNotificationManger(
        @ApplicationContext app: Context
    ) = (app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)

    @ServiceScoped
    @Provides
    fun provideNotificationsManger(
        baseNotificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManager
    ) = NotificationsManager(baseNotificationBuilder, notificationManager)

    @ServiceScoped
    @Provides
    fun provideLocationManager(
        fused: FusedLocationProviderClient
    ) = LocationController(fused)

    @Provides
    fun provideNoiseGetter(
    ) = NoiseGetter()

    @Provides
    fun provideNoiseGetterMockup(
    ) = NoiseGetterMockup()

    @Provides
    fun provideLocationSenderMockup(
    ) = LocationSenderMockup()

    @Provides
    fun provideLocationSender(
    ) = LocationSender()

    @Provides
    fun provideAudioManagerMockup() : AudioManagerMockup =
        AudioManagerMockup()

    @Provides
    fun provideAudioManager() : AudioManager =
        AudioManager()
}
