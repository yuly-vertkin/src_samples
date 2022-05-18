package ru.russianpost.payments.entities.tax

/** Данные из казначейства */
internal data class TreasuryData(
    val bic: String? = "",
    val bankName: String,
    val correspondentAccount: String,
    val recipientName: String = "",
    val recipientTreasuryNumber: String,
    val activeBankNumber: String,
)
