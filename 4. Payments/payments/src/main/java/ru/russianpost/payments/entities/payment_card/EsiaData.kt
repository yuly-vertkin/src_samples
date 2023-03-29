package ru.russianpost.payments.entities.payment_card

import ru.russianpost.payments.entities.BaseResponse

/** ЕСИА параметры */
internal data class EsiaParams (
    val uin: String?,
    val email: String?,
    val address: String? = null,
    val accessToken: String?,
    val idToken: String?,
)

/** ЕСИА данные */
internal data class EsiaData (
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val birthDate: String,
    val passportSeries: String,
    val passportNumber: String,
    val passportIssueDate: String,
    val passportIssuedBy: String,
    val passportIssueId: String,
    val addressRegStr: String?,
    val addressRegZipCode: String,
    val mobilephone: String,
    val mobileVrfStu: String,
) : BaseResponse()