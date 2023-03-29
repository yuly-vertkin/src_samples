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
    val document: String?,
) : Parcelable

