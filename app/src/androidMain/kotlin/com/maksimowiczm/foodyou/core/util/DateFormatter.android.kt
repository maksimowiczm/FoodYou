package com.maksimowiczm.foodyou.core.util

import android.content.Context
import android.text.format.DateFormat
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime

actual class DateFormatter(
    private val context: Context,
    private val androidSystemDetails: SystemDetails
) {
    private val defaultLocale: Locale
        get() = androidSystemDetails.defaultLocale

    actual val weekDayNamesShort: List<String>
        get() = DayOfWeek.entries.map {
            it.getDisplayName(TextStyle.SHORT, defaultLocale)
        }

    actual fun formatMonthYear(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", defaultLocale)
        return date.toJavaLocalDate().format(formatter)
    }

    actual fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", defaultLocale)
        return date.toJavaLocalDate().format(formatter)
    }

    actual fun formatDateShort(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", defaultLocale)
        return date.toJavaLocalDate().format(formatter)
    }

    actual fun formatTime(time: LocalTime): String = if (DateFormat.is24HourFormat(context)) {
        DateTimeFormatter
            .ofPattern("HH:mm", defaultLocale)
            .format(time.toJavaLocalTime())
    } else {
        DateTimeFormatter
            .ofPattern("hh:mm a", defaultLocale)
            .format(time.toJavaLocalTime())
    }
}
