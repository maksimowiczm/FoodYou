package com.maksimowiczm.foodyou.food.infrastructure

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.maksimowiczm.foodyou.food.domain.FoodName
import com.maksimowiczm.foodyou.food.domain.FoodNameSelector
import java.util.Locale

class AndroidFoodNameSelector(private val context: Context) : FoodNameSelector {
    override fun select(foodName: FoodName): String {
        val compat = AppCompatDelegate.getApplicationLocales()
        if (!compat.isEmpty) {
            for (i in 0 until compat.size()) {
                val locale = compat.get(i) ?: continue
                val name = localizedName(foodName, locale)
                if (name != null) {
                    return name
                }
            }
        }

        val config = context.resources.configuration.locales
        if (!config.isEmpty) {
            for (i in 0 until config.size()) {
                val locale = config.get(i) ?: continue
                val name = localizedName(foodName, locale)
                if (name != null) {
                    return name
                }
            }
        }

        val default = Locale.getDefault()
        val name = localizedName(foodName, default)
        if (name != null) {
            return name
        }

        return foodName.fallback
    }

    private fun localizedName(foodName: FoodName, locale: Locale): String? =
        when (locale.language) {
            "en" -> foodName.english
            "ca" -> foodName.catalan
            "da" -> foodName.danish
            "de" -> foodName.german
            "es" -> foodName.spanish
            "fr" -> foodName.french
            "it" -> foodName.italian
            "hu" -> foodName.hungarian
            "nl" -> foodName.dutch
            "pl" -> foodName.polish
            "pt" -> foodName.portugueseBrazil
            "tr" -> foodName.turkish
            "ru" -> foodName.russian
            "uk" -> foodName.ukrainian
            "ar" -> foodName.arabic
            "zh" -> foodName.chineseSimplified
            else -> null
        }
}
