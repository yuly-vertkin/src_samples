package ru.russianpost.payments.base.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R

/**
 * Основная Activity в которой содержатся все фрагменты модуля
 */
@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ps_activity_payments)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ps_ic_arrow_back_blue_24dp)
        setSupportActionBar(toolbar)
        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds = setOf(R.id.mainFragment, R.id.taxPaymentDoneFragment))
        NavigationUI.setupWithNavController(toolbar!!, navController, appBarConfiguration)
    }
}