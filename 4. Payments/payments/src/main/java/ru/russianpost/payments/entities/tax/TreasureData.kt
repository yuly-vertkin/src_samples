package ru.russianpost.payments.entities.tax

import androidx.annotation.Keep
import ru.russianpost.payments.entities.BaseResponse

/** Данные из казначейства */
@Keep
internal data class TreasuryData(
    val bic: String?,
    val bankName: String,
    val correspondentAccount: String,
    val recipientName: String,
    val recipientTreasuryNumber: String,
    val activeBankNumber: String,
) : BaseResponse()
