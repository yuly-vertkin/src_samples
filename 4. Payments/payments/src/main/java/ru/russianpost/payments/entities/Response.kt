package ru.russianpost.payments.entities

/** Классы представляющие результаты вызова API */
internal sealed class Response<out T> {
    object Loading: Response<Nothing>()
    data class Success<out T>(val data: T): Response<T>()
    data class Error(val error: Throwable): Response<Nothing>()
}
