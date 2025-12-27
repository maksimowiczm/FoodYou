package com.maksimowiczm.foodyou.common.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodSource

enum class FoodSourceType {
    User,
    OpenFoodFacts,
    USDA,
    SwissFoodCompositionDatabase,
    TACO,
    TBCA,
}

fun FoodSourceType.toDomain(): FoodSource.Type =
    when (this) {
        FoodSourceType.User -> FoodSource.Type.User
        FoodSourceType.OpenFoodFacts -> FoodSource.Type.OpenFoodFacts
        FoodSourceType.USDA -> FoodSource.Type.USDA
        FoodSourceType.SwissFoodCompositionDatabase -> FoodSource.Type.SwissFoodCompositionDatabase
        FoodSourceType.TACO -> FoodSource.Type.TACO
        FoodSourceType.TBCA -> FoodSource.Type.TBCA
    }

fun FoodSource.Type.toEntity(): FoodSourceType =
    when (this) {
        FoodSource.Type.User -> FoodSourceType.User
        FoodSource.Type.OpenFoodFacts -> FoodSourceType.OpenFoodFacts
        FoodSource.Type.USDA -> FoodSourceType.USDA
        FoodSource.Type.SwissFoodCompositionDatabase -> FoodSourceType.SwissFoodCompositionDatabase
        FoodSource.Type.TACO -> FoodSourceType.TACO
        FoodSource.Type.TBCA -> FoodSourceType.TBCA
    }
