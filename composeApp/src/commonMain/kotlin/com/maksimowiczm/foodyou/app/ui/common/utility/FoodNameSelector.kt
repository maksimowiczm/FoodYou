package com.maksimowiczm.foodyou.app.ui.common.utility

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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

fun OpenFoodFactsProduct.headline(nameSelector: FoodNameSelector) = buildString {
    append(nameSelector.select(name))
    if (brand != null) append(brand.let { " ($it)" })
}

fun UserProduct.headline(nameSelector: FoodNameSelector) = buildString {
    append(nameSelector.select(name))
    if (brand != null) append(brand.let { " ($it)" })
}

val LocalFoodNameSelector =
    staticCompositionLocalOf<FoodNameSelector> {
        object : FoodNameSelector {
            override fun select(foodName: FoodName): String = foodName.english ?: foodName.fallback

            override fun observeLanguage(): StateFlow<Language> = MutableStateFlow(Language.English)
        }
    }

@Composable
fun FoodNameSelectorProvider(selector: FoodNameSelector, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFoodNameSelector provides selector, content = content)
}
