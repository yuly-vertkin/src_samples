package ru.russianpost.payments.features.payment_card.domain

import android.content.Context
import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.entities.PaymentCard
import ru.russianpost.payments.entities.Response

/**
 * Repository оплаты картой
 */
internal interface PaymentCardRepository {
    fun getData() : PaymentCard
    fun saveData(data: PaymentCard?)
    fun saveCheckExt(context: Context, checkFileName: String) : Flow<Response<String>>
    fun saveCheckInt(context: Context, checkFileName: String) : Flow<Response<String>>
}