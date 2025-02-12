package com.maksimowiczm.foodyou.core.feature.system.data

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

interface StringFormatRepository {
    /**
     * The abbreviated names of the days of the week, ordered starting from the first day
     * of the week as defined by the system's locale.
     *
     * For example, in English (US), this could return `["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]`.
     */
    val weekDayNamesShort: List<String>

    /**
     * Formats the specified [date] as a string in the "LLLL yyyy" format.
     *
     * The formatting respects the system's locale, using the full month name and the year.
     *
     * @param date The date to format.
     * @return A string representing the formatted month and year.
     */
    fun formatMonthYear(date: LocalDate): String

    /**
     * Formats the specified [time] as a string in the "hh:mm" format.
     *
     * The formatting respects the system's locale.
     *
     * @param time The time to format.
     * @return A string representing the formatted time.
     */
    fun formatTime(time: LocalTime): String
}
