package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferences

data class FoodSearchPreferences(val openFoodFacts: OpenFoodFacts) :
    UserPreferences {
    data class OpenFoodFacts(val enabled: Boolean)

    val isOpenFoodFactsEnabled: Boolean
        get() = openFoodFacts.enabled
}
