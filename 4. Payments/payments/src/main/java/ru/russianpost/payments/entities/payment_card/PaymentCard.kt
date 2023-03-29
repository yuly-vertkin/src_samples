package ru.russianpost.payments.entities.payment_card

import ru.russianpost.payments.entities.BaseResponse

/** Реквизиты карты */
internal data class PaymentCard (
    val uin: String?,
    val cardUid: String?,
    val description: String,
    val redirectApprove: String,
    val redirectDecline: String,
    val redirectCancel: String,
) : BaseResponse()

internal data class PaymentCardResponse (
    val paymentReference: String,
) : BaseResponse()
