@file:Suppress("unused")

package ru.russianpost.payments.entities.payment_card

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.russianpost.android.protocols.auth.ExternalAuthProtocol.ExtAuthResult

/** Данные оплаты картой */
internal data class PaymentCardData (
    val uin: String? = null,
    val description: String? = null,
    val amount: Float? = null,
    val email: String? = null,
    val authResult: ExtAuthResult? = null,
    val cardUid: String? = null,
)

@Parcelize
data class CardDetail (
    val cardUid: String,
    val pan: String,
    val expiration: String,
    val brand: String,
) : Parcelable

data class CardDetails (
    val cards: List<CardDetail>? = null,
)

enum class PaymentStatus {
    CARD_DECLINE, CARD_CANCEL, AUTH_CANCEL
}

enum class PaymentDoneParams {
    NONE, SAVE_CHECK, SEND_CHECK
}

