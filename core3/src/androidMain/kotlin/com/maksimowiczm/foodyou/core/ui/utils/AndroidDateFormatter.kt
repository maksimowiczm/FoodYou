package com.maksimowiczm.foodyou.core.ui.utils

import android.content.Context
import android.text.format.DateFormat
import com.maksimowiczm.foodyou.infrastructure.android.defaultLocale
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

class AndroidDateFormatter(private val context: Context) : DateFormatter {
    private val defaultLocale: Locale
        get() = context.defaultLocale

    override val weekDayNamesShort: List<String>
        get() = DayOfWeek.entries.map {
            it.getDisplayName(TextStyle.SHORT, defaultLocale)
        }

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

    override fun formatTime(time: LocalTime): String = if (DateFormat.is24HourFormat(context)) {
        DateTimeFormatter
            .ofPattern("HH:mm", defaultLocale)
            .format(time.toJavaLocalTime())
    } else {
        DateTimeFormatter
            .ofPattern("hh:mm a", defaultLocale)
            .format(time.toJavaLocalTime())
    }

    override fun formatDateTime(dateTime: LocalDateTime): String {
        val pattern = if (DateFormat.is24HourFormat(context)) {
            "d MMMM yyyy, HH:mm"
        } else {
            "d MMMM yyyy, hh:mm a"
        }

        return DateTimeFormatter
            .ofPattern(pattern, defaultLocale)
            .format(dateTime.toJavaLocalDateTime())
    }
}
