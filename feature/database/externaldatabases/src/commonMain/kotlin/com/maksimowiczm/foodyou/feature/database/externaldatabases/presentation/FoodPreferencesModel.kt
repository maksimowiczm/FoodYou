package com.maksimowiczm.foodyou.feature.database.externaldatabases.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.food.domain.entity.FoodSearchPreferences

@Immutable
internal data class FoodPreferencesModel(
    val useOpenFoodFacts: Boolean? = null,
    val useUsda: Boolean? = null,
) {
    constructor(
        domain: FoodSearchPreferences
    ) : this(useOpenFoodFacts = domain.isOpenFoodFactsEnabled, useUsda = domain.isUsdaEnabled)
}
