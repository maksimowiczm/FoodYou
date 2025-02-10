package com.maksimowiczm.foodyou.core.feature.system.data

import android.content.Context
import com.maksimowiczm.foodyou.core.feature.system.data.model.Country
import kotlinx.datetime.toJavaLocalDate
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

    override val defaultCountry: Country
        get() = Country(
            name = defaultLocale.displayCountry,
            code = defaultLocale.country
        )

    override val countries: List<Country>
        get() = Locale.getISOCountries().map {
            Country(
                name = Locale("", it).displayCountry,
                code = it
            )
        }

    override val weekDayNamesShort: Array<String>
        get() = DayOfWeek.entries.map {
            it.getDisplayName(TextStyle.SHORT, defaultLocale)
        }.toTypedArray()

    override fun formatMonthYear(date: kotlinx.datetime.LocalDate) =
        formatMonthYear(date.toJavaLocalDate())

    private fun formatMonthYear(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", defaultLocale)
        return date.format(formatter)
    }
}
