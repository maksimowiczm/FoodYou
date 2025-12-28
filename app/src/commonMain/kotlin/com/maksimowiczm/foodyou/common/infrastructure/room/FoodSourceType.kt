package com.maksimowiczm.foodyou.common.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodSource

enum class FoodSourceType {
    User,
    OpenFoodFacts,
    TBCA,
}

fun FoodSourceType.toDomain(): FoodSource.Type =
    when (this) {
        FoodSourceType.User -> FoodSource.Type.User
        FoodSourceType.OpenFoodFacts -> FoodSource.Type.OpenFoodFacts
        FoodSourceType.TBCA -> FoodSource.Type.TBCA
    }

fun FoodSource.Type.toEntity(): FoodSourceType =
    when (this) {
        FoodSource.Type.User -> FoodSourceType.User
        FoodSource.Type.OpenFoodFacts -> FoodSourceType.OpenFoodFacts
        FoodSource.Type.TBCA -> FoodSourceType.TBCA
    }
