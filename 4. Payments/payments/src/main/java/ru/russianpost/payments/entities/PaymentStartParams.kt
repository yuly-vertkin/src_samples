package ru.russianpost.payments.entities

import ru.russianpost.payments.entities.payment_card.PaymentStatus

/** Стартовые параметры платежа */
internal data class PaymentStartParams (
    val id: String? = null,
    val paymentStatus: PaymentStatus? = null,
    val barcodeInfo: Map<String, String>? = null,
)