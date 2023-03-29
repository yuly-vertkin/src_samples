package ru.russianpost.payments.entities.payment_card

import ru.russianpost.payments.entities.BaseResponse

internal data class Commission (
    val transferSum: Float,
    val transferFee: Float,
    val totalSum: Float,
) : BaseResponse()
