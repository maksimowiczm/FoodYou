package com.maksimowiczm.foodyou.common.infrastructure

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

val Context.defaultLocale: Locale
    get() {
        val compat = AppCompatDelegate.getApplicationLocales()
        if (!compat.isEmpty) {
            for (i in 0 until compat.size()) {
                val locale = compat.get(i) ?: continue
                return locale
            }
        }

        val config = resources.configuration.locales
        if (!config.isEmpty) {
            for (i in 0 until config.size()) {
                val locale = config.get(i) ?: continue
                return locale
            }
        }

        val fallback = Locale.getDefault()

        return fallback
    }
