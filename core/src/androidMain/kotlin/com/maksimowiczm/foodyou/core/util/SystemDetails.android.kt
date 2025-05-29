package com.maksimowiczm.foodyou.core.util

import android.content.Context
import com.maksimowiczm.foodyou.infrastructure.android.defaultLocale
import java.util.Locale

actual class SystemDetails(private val context: Context) {
    val defaultLocale: Locale
        get() = context.defaultLocale

    actual val defaultCountry: Country
        get() = Country(
            name = defaultLocale.displayCountry,
            code = defaultLocale.country
        )
}
