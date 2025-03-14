package com.maksimowiczm.foodyou.data

import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime

actual class StringFormatRepository {
    val defaultLocale: Locale = Locale.getDefault()!!

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

    actual fun formatTime(time: LocalTime): String = DateTimeFormatter
        .ofPattern("HH:mm", defaultLocale)
        .format(time.toJavaLocalTime())
}
