package com.maksimowiczm.foodyou.common.infrastructure.food

import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.infrastructure.SystemDetails
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AndroidFoodNameSelector(private val systemDetails: SystemDetails) :
    FoodNameSelector {
    override fun select(foodName: FoodName): String {
        val language = localizedLanguage(systemDetails.defaultLocaleFlow.value) ?: Language.English
        return foodName[language] ?: foodName.fallback
    }

    override fun observeLanguage(): Flow<Language> =
        systemDetails.defaultLocaleFlow.map { localizedLanguage(it) ?: Language.English }

    private fun localizedLanguage(locale: Locale): Language? =
        when (locale.language) {
            "en" -> Language.English
            "ca" -> Language.Catalan
            "cs" -> Language.Czech
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
