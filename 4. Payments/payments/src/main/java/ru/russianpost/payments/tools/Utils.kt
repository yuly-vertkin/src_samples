package ru.russianpost.payments.tools

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

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

internal fun formatServerDate(dateStr: String) =
    formatDateStr(dateStr,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()),
        SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    ).replaceFirstChar(Char::titlecase)

internal fun formatReverseDate(dateStr: String) =
    formatDateStr(dateStr,
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    ).replaceFirstChar(Char::titlecase)

internal fun getMonthFromDate(dateStr: String) =
    formatDateStr(dateStr,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()),
        SimpleDateFormat("LLLL", Locale.getDefault())
    ).replaceFirstChar(Char::titlecase)

internal fun getDayFromDate(dateStr: String) =
    formatDateStr(dateStr,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()),
        SimpleDateFormat("EEE", Locale.getDefault())
    )

internal fun getTimeFromDate(dateStr: String) =
    formatDateStr(dateStr,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()),
        SimpleDateFormat("HH:mm", Locale.getDefault())
    )

internal fun formatDateStr(dateStr: String, inputFormat: SimpleDateFormat, outputFormat: SimpleDateFormat) : String {
    var result: String = dateStr

    try {
        inputFormat.parse(dateStr)?.let {
            result = outputFormat.format(it)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return result
}

internal fun getDateFromStr(dateStr: String, inputFormat: SimpleDateFormat) =
    try { inputFormat.parse(dateStr) } catch (e: ParseException) { null }
