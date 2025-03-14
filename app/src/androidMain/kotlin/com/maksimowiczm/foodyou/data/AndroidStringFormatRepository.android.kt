package com.maksimowiczm.foodyou.data

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

class AndroidStringFormatRepository(
    private val context: Context,
    private val androidSystemInfoRepository: AndroidSystemInfoRepository
) : StringFormatRepository {
    private val defaultLocale: Locale
        get() = androidSystemInfoRepository.defaultLocale

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
