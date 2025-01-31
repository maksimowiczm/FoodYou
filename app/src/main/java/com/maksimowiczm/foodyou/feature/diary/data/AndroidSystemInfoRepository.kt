package com.maksimowiczm.foodyou.feature.diary.data

import android.content.Context
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

internal class AndroidSystemInfoRepository(
    private val context: Context
) : SystemInfoRepository {
    private val defaultLocale: Locale
        get() = context.resources.configuration.locales[0]

    override val defaultCountryCode: String
        get() = defaultLocale.country

    override val weekDayNamesShort: Array<String>
        get() = DayOfWeek.entries.map {
            it.getDisplayName(TextStyle.SHORT, defaultLocale)
        }.toTypedArray()

    override fun formatMonthYear(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", defaultLocale)
        return date.format(formatter)
    }
}
