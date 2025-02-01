package com.maksimowiczm.foodyou.feature.system.data

import com.maksimowiczm.foodyou.feature.system.data.model.Country
import java.time.LocalDate

/**
 * Provides information about system-specific settings and utilities for date and locale operations.
 */
interface SystemInfoRepository {

    /**
     * The default country code of the system's locale.
     *
     * This value is derived from the system configuration and represents the ISO 3166-1 alpha-2 country code.
     */
    val defaultCountry: Country

    /**
     * The list of countries available on the system.
     */
    val countries: List<Country>

    /**
     * The abbreviated names of the days of the week, ordered starting from the first day
     * of the week as defined by the system's locale.
     *
     * For example, in English (US), this could return `["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]`.
     */
    val weekDayNamesShort: Array<String>

    /**
     * Formats the specified [date] as a string in the "LLLL yyyy" format.
     *
     * The formatting respects the system's locale, using the full month name and the year.
     *
     * @param date The date to format.
     * @return A string representing the formatted month and year.
     */
    fun formatMonthYear(date: LocalDate): String
}
