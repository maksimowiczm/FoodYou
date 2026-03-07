package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.YearMonth

interface DateFormatter {
    /**
     * The abbreviated names of the days of the week, ordered starting from Monday.
     *
     * For example, in English (US), this could return `["Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
     * "Sun"]`.
     */
    val weekDayNamesShort: List<String>

    /**
     * Formats the specified [date] as a string in the "LLLL yyyy" format.
     *
     * The formatting respects the system's locale, using the full month name and the year.
     *
     * For example, in English (US), this could return "February 2025".
     *
     * @param date The date to format.
     * @return A string representing the formatted month and year.
     */
    fun formatMonthYear(date: LocalDate): String

    fun formatMonthYear(date: YearMonth): String = formatMonthYear(date.firstDay)

    /**
     * Formats the specified [date] as a string in the "d MMMM yyyy, EEEE" format.
     *
     * For example, in English (US), this could return "23 February 2025, Sunday".
     */
    fun formatDate(date: LocalDate): String

    /**
     * Formats the specified [date] as a string in the "d MMMM yyyy" format.
     *
     * For example, in English (US), this could return "21 April 2025".
     */
    fun formatDateShort(date: LocalDate): String

    /**
     * Formats the specified [date] as a string in the "d.M.yy" format. This format uses digits only
     * and does not include the month name.
     */
    fun formatDateSuperShort(date: LocalDate): String

    /**
     * Formats the specified [time] as a string in the "hh:mm" format.
     *
     * The formatting respects the system's locale.
     *
     * @param time The time to format.
     * @return A string representing the formatted time.
     */
    fun formatTime(time: LocalTime): String

    /**
     * Formats the specified [dateTime] as a string in the "d MMMM yyyy, hh:mm" format.
     *
     * For example, in English (US), this could return "21 April 2025, 14:30".
     */
    fun formatDateTime(dateTime: LocalDateTime): String
}

private val defaultDateFormatter: DateFormatter =
    object : DateFormatter {
        override val weekDayNamesShort: List<String>
            get() = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        override fun formatMonthYear(date: LocalDate): String = date.toString()

        override fun formatDate(date: LocalDate): String = date.toString()

        override fun formatDateShort(date: LocalDate): String = date.toString()

        override fun formatDateSuperShort(date: LocalDate): String = date.toString()

        override fun formatTime(time: LocalTime): String = time.toString()

        override fun formatDateTime(dateTime: LocalDateTime): String = dateTime.toString()
    }

val LocalDateFormatter = staticCompositionLocalOf { defaultDateFormatter }

@Composable
fun DateFormatterProvider(dateFormatter: DateFormatter, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDateFormatter provides dateFormatter) { content() }
}
