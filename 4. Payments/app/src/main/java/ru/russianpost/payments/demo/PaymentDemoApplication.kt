package ru.russianpost.payments.demo

import android.app.Application
import ru.russianpost.payments.PaymentContract
import timber.log.Timber
import timber.log.Timber.DebugTree

class PaymentDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        PaymentContract.initPayments(
            appContext = this@PaymentDemoApplication,
            authController = AuthorisationControllerImp(this@PaymentDemoApplication),
            externalAuth = ExternalAuthProtocolImp(this@PaymentDemoApplication),
            retrofitBuilder = RetrofitBuilderImp,
            okHttpClient = provideOkHttpClient(),
            endPoint = RetrofitBuilderImp.BASE_URL,
            analytics = AnalyticsProtocolImp(),
            isTestVersion = true,
        )
    }

    companion object {
        lateinit var instance: PaymentDemoApplication
            private set
    }
}