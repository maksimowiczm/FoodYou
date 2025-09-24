package com.maksimowiczm.foodyou.common.compose.utility

import android.content.Context
import android.text.format.DateFormat
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toJavaLocalTime

fun interface LocalProvider {
    fun getCurrentLocale(): Locale
}

class AndroidDateFormatter(private val context: Context, private val localProvider: LocalProvider) :
    DateFormatter {
    private val defaultLocale: Locale
        get() = localProvider.getCurrentLocale()

    override val weekDayNamesShort: List<String>
        get() = DayOfWeek.entries.map { it.getDisplayName(TextStyle.SHORT, defaultLocale) }

    override fun formatMonthYear(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", defaultLocale)
        return date.toJavaLocalDate().format(formatter)
    }

    override fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", defaultLocale)
        return date.toJavaLocalDate().format(formatter)
    }

    override fun formatDateShort(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", defaultLocale)
        return date.toJavaLocalDate().format(formatter)
    }

    override fun formatDateSuperShort(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("d.M.yy", defaultLocale)
        return date.toJavaLocalDate().format(formatter)
    }

    override fun formatTime(time: LocalTime): String =
        if (DateFormat.is24HourFormat(context)) {
            DateTimeFormatter.ofPattern("HH:mm", defaultLocale).format(time.toJavaLocalTime())
        } else {
            DateTimeFormatter.ofPattern("hh:mm a", defaultLocale).format(time.toJavaLocalTime())
        }

    override fun formatDateTime(dateTime: LocalDateTime): String {
        val pattern =
            if (DateFormat.is24HourFormat(context)) {
                "d MMMM yyyy, HH:mm"
            } else {
                "d MMMM yyyy, hh:mm a"
            }

        return DateTimeFormatter.ofPattern(pattern, defaultLocale)
            .format(dateTime.toJavaLocalDateTime())
    }
}
