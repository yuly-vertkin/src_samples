package ru.russianpost.payments.base

import android.content.Context
import ru.russianpost.payments.data.network.setupCoil

fun initPayments(context: Context) {
    setupCoil(context)
}
