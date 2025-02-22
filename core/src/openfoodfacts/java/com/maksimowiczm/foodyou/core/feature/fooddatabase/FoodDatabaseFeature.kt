package com.maksimowiczm.foodyou.core.feature.fooddatabase

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.OpenFoodFactsFeature

val FoodDatabaseFeature = OpenFoodFactsFeature

fun NavController.navigateToFoodDatabaseSettings(navOptions: NavOptions? = null) {
    with(OpenFoodFactsFeature) {
        navigateToOpenFoodFactsSettings(navOptions)
    }
}
