package com.maksimowiczm.foodyou.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.maksimowiczm.foodyou.data.model.Country
import java.util.Locale

class AndroidSystemInfoRepository(private val context: Context) : SystemInfoRepository {
    val defaultLocale: Locale
        get() {
            val compat = AppCompatDelegate.getApplicationLocales().get(0)
            if (compat != null) {
                return compat
            }

            val config = context.resources.configuration.locales.get(0)

            if (config != null) {
                return config
            }

            val fallback = Locale.getDefault()

            return fallback
        }

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
