package ru.russianpost.payments.entities.advices

import ru.russianpost.payments.entities.BaseResponse

/** Данные для подсказки */
internal data class AdviceData (
    val id: String,
    val title: String,
    val img: String? = null,
    val desc: String,
    val link: String? = null,
) : BaseResponse()
