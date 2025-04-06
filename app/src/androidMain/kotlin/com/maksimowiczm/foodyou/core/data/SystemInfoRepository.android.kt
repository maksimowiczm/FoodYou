package com.maksimowiczm.foodyou.core.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

actual class SystemInfoRepository(private val context: Context) {
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
