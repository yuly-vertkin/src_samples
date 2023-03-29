package ru.russianpost.payments.demo

import okhttp3.Interceptor
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor

class DebugOkHttpHelper {

    companion object {
        private const val MAX_CONTENT_SIZE = 10000

        private val LOGGING = HttpLoggingInterceptor {
            val message = if (it.length < MAX_CONTENT_SIZE) {
                it
            } else {
                "${it.take(MAX_CONTENT_SIZE)} *** ${it.length - MAX_CONTENT_SIZE} BYTES REDUCED ***"
            }
            logOkHttpClient(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        fun getInterceptor(): Interceptor {
            return LOGGING
        }

        private fun logOkHttpClient(message: String) {
            Platform.get().log(message)
        }
    }
}