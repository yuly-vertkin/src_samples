package ru.russianpost.payments.features.payment_card.domain

import android.content.Context
import kotlinx.coroutines.flow.Flow
import ru.russianpost.payments.entities.BaseResponse
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.entities.payment_card.Commission
import ru.russianpost.payments.entities.payment_card.*

/**
 * Repository оплаты картой
 */
internal interface PaymentCardRepository {
    fun getData() : PaymentCardData
    fun saveData(data: PaymentCardData?)
    fun getCards() : Flow<Result<CardDetails>>
    fun paymentAvailabilityCheck(uin: String) : Flow<Result<BaseResponse>>
    fun getEsiaData(params: EsiaParams) : Flow<Result<EsiaData>>
    fun getCommission(uin: String) : Flow<Result<Commission>>
    fun paymentCard(card: PaymentCard) : Flow<Result<PaymentCardResponse>>
    fun createReceipt(receipt: Receipt) : Flow<Result<String>>
    fun deletePaymentLink(uin: String) : Flow<Result<String>>
    fun saveCheckExt(context: Context, checkFileName: String) : Flow<Result<String>>
    fun saveCheckInt(context: Context, checkFileName: String) : Flow<Result<String>>
}