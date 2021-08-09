package it.unibo.giacche.contextaware

import android.app.Application
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        // Start timber for debug
        Timber.plant(Timber.DebugTree())
    }
}