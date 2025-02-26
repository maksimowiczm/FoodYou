package com.maksimowiczm.foodyou.data

import android.content.Context
import com.maksimowiczm.foodyou.data.model.Country
import java.util.Locale

internal class AndroidSystemInfoRepository(private val context: Context) : SystemInfoRepository {
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
}
