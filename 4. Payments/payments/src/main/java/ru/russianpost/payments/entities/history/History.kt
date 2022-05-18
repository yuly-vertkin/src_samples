package ru.russianpost.payments.entities.history

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

internal enum class PaymentType {
    Tax, AutoFine
}

/** Элемент истории платежа */
internal data class History (
    val id: String = "",
    val type: String = "",
    val title: String = "",
    @SerializedName("purpose") @Expose val desc: String = "",
    @SerializedName("offenseDate") @Expose val date: String = "",
    @SerializedName("totalAmount") @Expose val sum: Float = 0f,
)
