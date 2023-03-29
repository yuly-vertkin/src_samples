package ru.russianpost.payments.entities.history

/** Данные для истории платежей */
internal data class HistoryData (
    val taxFilter: Boolean = false,
    val fineFilter: Boolean = false,
    val period: String? = null,
)
