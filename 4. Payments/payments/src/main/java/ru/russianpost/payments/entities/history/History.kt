package ru.russianpost.payments.entities.history

import ru.russianpost.payments.entities.BaseResponse

internal enum class PaymentType {
    PAYMENT, TAX_PAYMENT, FINE_PAYMENT
}

/** Элемент истории платежа */
internal data class History (
    val id: String,
    val type: String,
    val purpose: String,
    val totalAmount: Float,
    val createdAt: String,
) : BaseResponse()
