package ru.russianpost.payments.base.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.R
import ru.russianpost.payments.base.di.DaggerPaymentComponent
import ru.russianpost.payments.base.di.PaymentComponent
import ru.russianpost.payments.base.di.PaymentModule

/**
 * Основная Activity в которой содержатся все фрагменты модуля
 */
internal class PaymentActivity : AppCompatActivity() {
    lateinit var component: PaymentComponent
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        component = DaggerPaymentComponent.builder()
            .paymentModule(PaymentModule(
                appContext = this.applicationContext,
                retrofitBuilder = PaymentContract.retrofitBuilder,
                externalAuth = PaymentContract.externalAuth,
            ))
            .build()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.ps_activity_payments)

        PaymentContract.startIntent = intent

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ps_ic_arrow_back_blue_24dp)
        setSupportActionBar(toolbar)
        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
    }
}