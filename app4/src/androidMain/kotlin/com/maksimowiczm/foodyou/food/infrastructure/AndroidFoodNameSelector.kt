package com.maksimowiczm.foodyou.food.infrastructure

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import java.util.Locale

class AndroidFoodNameSelector(private val context: Context) : FoodNameSelector {
    override fun select(foodName: FoodName): String {
        val language = select()
        return foodName[language] ?: foodName.fallback
    }

    override fun select(): Language {
        val compat = AppCompatDelegate.getApplicationLocales()
        if (!compat.isEmpty) {
            for (i in 0 until compat.size()) {
                val locale = compat.get(i) ?: continue
                val language = localizedLanguage(locale)
                if (language != null) {
                    return language
                }
            }
        }

        val config = context.resources.configuration.locales
        if (!config.isEmpty) {
            for (i in 0 until config.size()) {
                val locale = config.get(i) ?: continue
                val language = localizedLanguage(locale)
                if (language != null) {
                    return language
                }
            }
        }

        val default = Locale.getDefault()
        val language = localizedLanguage(default)
        if (language != null) {
            return language
        }

        return Language.English
    }

    private fun localizedLanguage(locale: Locale): Language? =
        when (locale.language) {
            "en" -> Language.English
            "ca" -> Language.Catalan
            "da" -> Language.Danish
            "de" -> Language.German
            "es" -> Language.Spanish
            "fr" -> Language.French
            "it" -> Language.Italian
            "hu" -> Language.Hungarian
            "nl" -> Language.Dutch
            "pl" -> Language.Polish
            "pt" -> Language.PortugueseBrazil
            "tr" -> Language.Turkish
            "ru" -> Language.Russian
            "uk" -> Language.Ukrainian
            "ar" -> Language.Arabic
            "zh" -> Language.ChineseSimplified
            else -> null
        }
}
