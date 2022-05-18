package ru.russianpost.payments.entities

/** Реквизиты карты */
internal data class PaymentCard (
    val cardNumber: String = "",
    val validThru: String = "",
    val cvc: String = "",
)
/*
internal data class PaymentCard (
    val cardNumber: String = "4561 2612 1234 5467",
    val validThru: String = "09/22",
    val cvc: String = "",
)
*/
