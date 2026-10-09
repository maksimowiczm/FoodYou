package com.maksimowiczm.foodyou.foodyou3.infrastructure

import com.maksimowiczm.foodyou.foodyou3.domain.FoodYou3FoodSource

enum class FoodYou3FoodSourceType {
    User,
    OpenFoodFacts,
    USDA,
    SwissFoodCompositionDatabase,
}

fun FoodYou3FoodSource.Type.toEntity(): FoodYou3FoodSourceType =
    when (this) {
        FoodYou3FoodSource.Type.User -> FoodYou3FoodSourceType.User
        FoodYou3FoodSource.Type.OpenFoodFacts -> FoodYou3FoodSourceType.OpenFoodFacts
        FoodYou3FoodSource.Type.USDA -> FoodYou3FoodSourceType.USDA
        FoodYou3FoodSource.Type.SwissFoodCompositionDatabase ->
            FoodYou3FoodSourceType.SwissFoodCompositionDatabase
    }
