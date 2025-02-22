package com.maksimowiczm.foodyou.core.feature.system.data

import android.content.Context
import android.text.format.DateFormat
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime

class AndroidStringFormatRepository(private val context: Context) : StringFormatRepository {
    private val defaultLocale: Locale
        get() = context.resources.configuration.locales[0]

    override val weekDayNamesShort: List<String>
        get() = DayOfWeek.entries.map {
            it.getDisplayName(TextStyle.SHORT, defaultLocale)
        }

    override fun formatMonthYear(date: kotlinx.datetime.LocalDate) =
        formatMonthYear(date.toJavaLocalDate())

    private fun formatMonthYear(date: java.time.LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", defaultLocale)
        return date.format(formatter)
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
}
