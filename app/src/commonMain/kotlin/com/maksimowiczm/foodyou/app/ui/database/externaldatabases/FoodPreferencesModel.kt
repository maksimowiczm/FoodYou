package com.maksimowiczm.foodyou.app.ui.database.externaldatabases

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferences

@Immutable
internal data class FoodPreferencesModel(
    val useOpenFoodFacts: Boolean? = null,
    val useUsda: Boolean? = null,
) {
    constructor(
        domain: FoodSearchPreferences
    ) : this(useOpenFoodFacts = domain.isOpenFoodFactsEnabled, useUsda = domain.isUsdaEnabled)
}
