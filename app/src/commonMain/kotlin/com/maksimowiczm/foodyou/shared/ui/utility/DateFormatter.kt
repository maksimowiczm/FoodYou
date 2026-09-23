package com.maksimowiczm.foodyou.shared.ui.utility

import androidx.compose.runtime.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.number

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

    /**
     * Formats the specified [date] as a string in the "d MMMM" format.
     *
     * The formatting respects the system's locale, using the day of the month and the full month
     *
     * For example, in English (US), this could return "23 February".
     *
     * @param date The date to format.
     * @return A string representing the formatted day and month.
     */
    fun formatDayMonth(date: LocalDate): String

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

expect class DateFormatterImpl : DateFormatter {
    override val weekDayNamesShort: List<String>

    override fun formatMonthYear(date: LocalDate): String

    override fun formatDayMonth(date: LocalDate): String

    override fun formatDate(date: LocalDate): String

    override fun formatDateShort(date: LocalDate): String

    override fun formatDateSuperShort(date: LocalDate): String

    override fun formatTime(time: LocalTime): String

    override fun formatDateTime(dateTime: LocalDateTime): String
}

/**
 * A minimal, locale-independent [DateFormatter] implementation used as a fallback when no
 * platform-specific formatter has been provided (e.g. in previews or before composition locals are
 * set up). It does not respect the system locale.
 */
private val defaultDateFormatter: DateFormatter =
    object : DateFormatter {
        override val weekDayNamesShort: List<String> =
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        private fun String.capitalizeFirst() = lowercase().replaceFirstChar { it.uppercase() }

        private fun monthName(date: LocalDate): String = date.month.name.capitalizeFirst()

        private fun dayOfWeekName(date: LocalDate): String = date.dayOfWeek.name.capitalizeFirst()

        private fun Int.pad2(): String = toString().padStart(2, '0')

        override fun formatMonthYear(date: LocalDate): String = "${monthName(date)} ${date.year}"

        override fun formatDayMonth(date: LocalDate): String = "${date.day} ${monthName(date)}"

        override fun formatDate(date: LocalDate): String =
            "${date.day} ${monthName(date)} ${date.year}, ${dayOfWeekName(date)}"

        override fun formatDateShort(date: LocalDate): String =
            "${date.day} ${monthName(date)} ${date.year}"

        override fun formatDateSuperShort(date: LocalDate): String {
            val shortYear = date.year % 100
            return "${date.day}.${date.month.number}.${shortYear.pad2()}"
        }

        override fun formatTime(time: LocalTime): String =
            "${time.hour.pad2()}:${time.minute.pad2()}"

        override fun formatDateTime(dateTime: LocalDateTime): String =
            "${formatDateShort(dateTime.date)}, ${formatTime(dateTime.time)}"
    }

val LocalDateFormatter = staticCompositionLocalOf { defaultDateFormatter }
