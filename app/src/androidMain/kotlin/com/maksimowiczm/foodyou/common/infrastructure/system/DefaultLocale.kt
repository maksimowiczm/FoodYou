package com.maksimowiczm.foodyou.common.infrastructure.system

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

val Context.defaultLocale: Locale
    get() {
        val compat = AppCompatDelegate.getApplicationLocales().get(0)
        if (compat != null) {
            return compat
        }

        val config = resources.configuration.locales.get(0)

        if (config != null) {
            return config
        }

        val fallback = Locale.getDefault()

        return fallback
    }
