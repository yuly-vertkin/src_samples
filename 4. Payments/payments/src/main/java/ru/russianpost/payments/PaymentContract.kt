package ru.russianpost.payments

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import okhttp3.OkHttpClient
import ru.russianpost.android.protocols.analytics.AnalyticsProtocol
import ru.russianpost.android.protocols.auth.AuthorisationController
import ru.russianpost.android.protocols.auth.ExternalAuthProtocol
import ru.russianpost.android.protocols.http.RetrofitBuilder
import ru.russianpost.payments.base.ui.PaymentActivity

object PaymentContract {
    lateinit var appContext: Context
        private set
    lateinit var authController: AuthorisationController
        private set
    lateinit var externalAuth: ExternalAuthProtocol
        private set
    lateinit var retrofitBuilder: RetrofitBuilder
        private set
    lateinit var okHttpClient: OkHttpClient
        private set
    lateinit var endPoint: String
        private set
    lateinit var analytics: AnalyticsProtocol
        private set
    var isTestVersion = true
        private set
    var startIntent: Intent? = null

//    fun getPaymentFragment() : PaymentFragment = PaymentFragment()

    fun getPaymentActivityIntent(context: Context) = Intent(context, PaymentActivity::class.java)

    fun initPayments(
        appContext: Context,
        authController: AuthorisationController,
        externalAuth: ExternalAuthProtocol,
        retrofitBuilder: RetrofitBuilder,
        okHttpClient: OkHttpClient,
        endPoint: String,
        analytics: AnalyticsProtocol,
        isTestVersion: Boolean,
    ) {
        this.appContext = appContext
        this.authController = authController
        this.externalAuth = externalAuth
        this.retrofitBuilder = retrofitBuilder
        this.okHttpClient = okHttpClient
        this.endPoint = endPoint
        this.analytics = analytics
        this.isTestVersion = isTestVersion
    }
}

class PaymentFileProvider : FileProvider()