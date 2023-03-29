package ru.russianpost.payments.tools

import ru.russianpost.payments.base.ui.LOG_TAG
import timber.log.Timber

internal object Log {
    fun d(message: String?, vararg args: Any?) =
        Timber.tag(LOG_TAG).d(message, args)

    fun e(t: Throwable?) =
        Timber.tag(LOG_TAG).e(t)
}