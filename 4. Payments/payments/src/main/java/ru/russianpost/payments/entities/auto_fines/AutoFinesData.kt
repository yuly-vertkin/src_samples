package ru.russianpost.payments.entities.auto_fines

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Данные для поиска штрафов */

enum class AutoFineType {
    VRC, DL
}

@Parcelize
data class AutoFineEditDocumentParam (
    val type: AutoFineType,
    val document: String = "",
) : Parcelable

@Parcelize
data class AutoFinesData (
    val vehicleRegistrationCertificates: List<String>? = null, //"7844478122",
    val driverLicenses: List<String>? = null, //"8221168066",
    val saveDocuments: Boolean = true,
    val currentFineId: String = "",
) : Parcelable

