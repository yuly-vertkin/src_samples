package ru.russianpost.payments.demo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.russianpost.payments.base.initPayments

@HiltAndroidApp
class PaymentDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initPayments(this@PaymentDemoApplication)
    }
}