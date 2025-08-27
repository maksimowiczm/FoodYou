package com.maksimowiczm.foodyou.business.food.domain

data class FoodSearchPreferences(val openFoodFacts: OpenFoodFacts, val usda: Usda) {
    data class OpenFoodFacts(val enabled: Boolean)

    data class Usda(val enabled: Boolean, val apiKey: String?)

    val isOpenFoodFactsEnabled: Boolean
        get() = openFoodFacts.enabled

    val isUsdaEnabled: Boolean
        get() = usda.enabled
}
