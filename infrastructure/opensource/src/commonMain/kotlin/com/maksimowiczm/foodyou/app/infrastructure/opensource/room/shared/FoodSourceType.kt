package com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared

import com.maksimowiczm.foodyou.shared.domain.food.FoodSource

enum class FoodSourceType {
    User,
    OpenFoodFacts,
    USDA,
    SwissFoodCompositionDatabase,
}

fun FoodSourceType.toDomain(): FoodSource.Type =
    when (this) {
        FoodSourceType.User -> FoodSource.Type.User
        FoodSourceType.OpenFoodFacts -> FoodSource.Type.OpenFoodFacts
        FoodSourceType.USDA -> FoodSource.Type.USDA
        FoodSourceType.SwissFoodCompositionDatabase -> FoodSource.Type.SwissFoodCompositionDatabase
    }

fun FoodSource.Type.toEntity(): FoodSourceType =
    when (this) {
        FoodSource.Type.User -> FoodSourceType.User
        FoodSource.Type.OpenFoodFacts -> FoodSourceType.OpenFoodFacts
        FoodSource.Type.USDA -> FoodSourceType.USDA
        FoodSource.Type.SwissFoodCompositionDatabase -> FoodSourceType.SwissFoodCompositionDatabase
    }
