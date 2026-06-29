package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Service for selecting appropriate food names based on user locale preferences.
 *
 * This interface handles the selection of localized food names from multilingual food data,
 * ensuring users see food names in their preferred language or a suitable fallback.
 */
interface FoodNameSelector {

    /**
     * Selects the most appropriate food name based on the user's locale preferences.
     *
     * @param foodName The multilingual food name data
     * @return The food name string in the user's preferred language or fallback
     */
    fun select(foodName: FoodName): String

    /**
     * Observes the most appropriate food name language based on the user's locale preferences.
     *
     * @return A flow emitting the preferred language for food names
     */
    fun observeLanguage(): Flow<Language>
}

internal class FoodNameSelectorImpl(private val systemDetails: SystemDetails) : FoodNameSelector {
    override fun select(foodName: FoodName): String {
        val tag = runBlocking { systemDetails.languageTag.first() }
        val language = localizedLanguage(tag) ?: Language.English
        return foodName[language] ?: foodName.fallback
    }

    override fun observeLanguage(): Flow<Language> =
        systemDetails.languageTag.map { localizedLanguage(it) ?: Language.English }

    private fun localizedLanguage(tag: String): Language? = Language.fromTag(tag)
}

fun OpenFoodFactsProduct.headline(nameSelector: FoodNameSelector) = buildString {
    append(nameSelector.select(name))
    if (brand != null) append(brand.let { " ($it)" })
}

fun UserProduct.headline(nameSelector: FoodNameSelector) = buildString {
    append(nameSelector.select(name))
    if (brand != null) append(brand.let { " ($it)" })
}

fun SearchResult.UserProduct.headline(nameSelector: FoodNameSelector) = buildString {
    append(nameSelector.select(name))
    if (brand != null) append(brand.let { " ($it)" })
}

fun SearchResult.UserRecipe.headline(nameSelector: FoodNameSelector) = nameSelector.select(name)

fun UserRecipe.headline(nameSelector: FoodNameSelector) = nameSelector.select(name)

val LocalFoodNameSelector =
    staticCompositionLocalOf<FoodNameSelector> {
        object : FoodNameSelector {
            override fun select(foodName: FoodName): String = foodName.english ?: foodName.fallback

            override fun observeLanguage(): StateFlow<Language> = MutableStateFlow(Language.English)
        }
    }
