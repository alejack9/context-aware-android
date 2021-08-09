package it.unibo.giacche.contextaware.communication

import okhttp3.OkHttpClient

object OkHttpSingleton {
    val INSTANCE = OkHttpClient()
}
