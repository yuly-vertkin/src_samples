package ru.russianpost.payments.entities

/** Классы представляющие результаты вызова API */
internal sealed class Result<out T> {
    object Loading: Result<Nothing>()
    data class Success<out T>(val data: T): Result<T>()
    data class Error(val error: Throwable): Result<Nothing>()
}
