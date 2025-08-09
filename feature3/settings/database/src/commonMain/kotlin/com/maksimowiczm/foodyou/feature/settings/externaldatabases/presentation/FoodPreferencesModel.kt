package com.maksimowiczm.foodyou.feature.settings.externaldatabases.presentation

import com.maksimowiczm.foodyou.business.food.domain.FoodPreferences

internal data class FoodPreferencesModel(
    val useOpenFoodFacts: Boolean? = null,
    val useUsda: Boolean? = null,
) {
    constructor(
        domain: FoodPreferences
    ) : this(useOpenFoodFacts = domain.isOpenFoodFactsEnabled, useUsda = domain.isUsdaEnabled)
}
