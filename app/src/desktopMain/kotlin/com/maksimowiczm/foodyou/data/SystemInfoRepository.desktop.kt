package com.maksimowiczm.foodyou.data

import com.maksimowiczm.foodyou.data.model.Country
import java.util.Locale

actual class SystemInfoRepository {
    val defaultLocale: Locale
        get() = Locale.getDefault()

    actual val defaultCountry: Country
        get() = Country(
            name = defaultLocale.displayCountry,
            code = defaultLocale.country
        )

    actual val countries: List<Country>
        get() = Locale.getISOCountries().map {
            Country(
                name = Locale("", it).displayCountry,
                code = it
            )
        }
}
