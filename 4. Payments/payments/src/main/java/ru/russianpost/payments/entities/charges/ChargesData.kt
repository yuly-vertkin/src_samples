package ru.russianpost.payments.entities.charges

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Данные для поиска начислений */
@Parcelize
data class ChargesData (
    val uin: String? = null,
    val inn: Set<String>? = null,
    val vehicleRegistrationCertificates: Set<String>? = null,
    val driverLicenses: Set<String>? = null,
    val saveDocuments: Boolean = true,
    val updateDocuments: Boolean = false,
) : Parcelable

