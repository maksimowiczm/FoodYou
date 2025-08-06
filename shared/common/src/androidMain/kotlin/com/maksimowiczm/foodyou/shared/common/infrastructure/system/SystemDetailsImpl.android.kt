package com.maksimowiczm.foodyou.shared.common.infrastructure.system

import android.content.Context
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.system.Country
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.system.SystemDetails
import java.util.Locale

actual class SystemDetailsImpl(private val context: Context) : SystemDetails {
    val defaultLocale: Locale
        get() = context.defaultLocale

    actual override val defaultCountry: Country
        get() = Country(name = defaultLocale.displayCountry, code = defaultLocale.country)
}
