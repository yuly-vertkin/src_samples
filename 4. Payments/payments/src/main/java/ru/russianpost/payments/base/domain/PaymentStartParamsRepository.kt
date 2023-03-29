package ru.russianpost.payments.base.domain

import ru.russianpost.payments.entities.PaymentStartParams

/**
 * Repository стартовых параметров платежа
 */
internal interface PaymentStartParamsRepository {
    fun getData() : PaymentStartParams
    fun saveData(data: PaymentStartParams?)
}