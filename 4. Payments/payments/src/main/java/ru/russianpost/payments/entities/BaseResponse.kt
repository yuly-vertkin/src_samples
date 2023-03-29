package ru.russianpost.payments.entities

open class BaseResponse(val errorCode: Int? = null, val errorTitle: String? = null, val errorMessage: String? = null)

class ResponseException(val errorCode: Int, val errorTitle: String? = null, val errorMessage: String? = null) : Exception()
