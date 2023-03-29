package ru.russianpost.payments.tools

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

internal const val YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss"
internal const val YYYY = "yyyy"
internal const val LLLL = "LLLL"
internal const val EEE = "EEE"
internal const val DD_MM_YYYY = "dd.MM.yyyy"
internal const val YYYY_MM_DD = "yyyy-MM-dd"
internal const val DD_MM_YYYY_HH_MM_SS = "dd.MM.yyyy HH:mm:ss"
internal const val HH_MM = "HH:mm"

internal fun isToday(time: Long): Boolean {
    return DateUtils.isToday(time)
}

internal fun isDaysBefore(time: Long, days: Int): Boolean {
    for (i in 1..days) {
        if (DateUtils.isToday(time + i * DateUtils.DAY_IN_MILLIS))
            return true
    }
    return false
}

internal fun formatServerDate(dateStr: String?) =
    formatDateStr(dateStr,
        SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS, Locale.getDefault()),
        SimpleDateFormat(DD_MM_YYYY_HH_MM_SS, Locale.getDefault())
    ).replaceFirstChar(Char::titlecase)

internal fun formatReverseDate(dateStr: String?) =
    formatDateStr(dateStr,
        SimpleDateFormat(YYYY_MM_DD, Locale.getDefault()),
        SimpleDateFormat(DD_MM_YYYY, Locale.getDefault())
    ).replaceFirstChar(Char::titlecase)

internal fun getMonthFromDate(dateStr: String?) =
    formatDateStr(dateStr,
        SimpleDateFormat(YYYY_MM_DD, Locale.getDefault()),
        SimpleDateFormat(LLLL, Locale.getDefault())
    ).replaceFirstChar(Char::titlecase)

internal fun getDayFromDate(dateStr: String?) =
    formatDateStr(dateStr,
        SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS, Locale.getDefault()),
        SimpleDateFormat(EEE, Locale.getDefault())
    )

internal fun getTimeFromDate(dateStr: String?) =
    formatDateStr(dateStr,
        SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS, Locale.getDefault()),
        SimpleDateFormat(HH_MM, Locale.getDefault())
    )

internal fun getYearFromDate(dateStr: String?) =
    formatDateStr(dateStr,
        SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS, Locale.getDefault()),
        SimpleDateFormat(YYYY, Locale.getDefault())
    )

internal fun formatDateStr(dateStr: String?, inputFormat: SimpleDateFormat, outputFormat: SimpleDateFormat) : String {
    var result: String = dateStr.orEmpty()

    try {
        inputFormat.parse(dateStr.orEmpty())?.let {
            result = outputFormat.format(it)
        }
    } catch (e: ParseException) { /* nothing to do */ }
    return result
}

internal fun getDateFromStr(dateStr: String?, inputFormat: SimpleDateFormat) =
    try { inputFormat.parse(dateStr.orEmpty()) } catch (e: ParseException) { null }

internal fun formatSimpleDate(date: Date) : String =
    formatDate(date, SimpleDateFormat(DD_MM_YYYY, Locale.getDefault()))

internal fun formatDate(date: Date, outputFormat: SimpleDateFormat) : String =
    outputFormat.format(date)
