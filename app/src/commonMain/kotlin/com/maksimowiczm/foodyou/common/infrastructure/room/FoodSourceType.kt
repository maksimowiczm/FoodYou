package com.maksimowiczm.foodyou.common.infrastructure.room

import com.maksimowiczm.foodyou.common.domain.food.FoodSource

enum class FoodSourceType {
    User,
    OpenFoodFacts,
<<<<<<< Updated upstream
    USDA,
    SwissFoodCompositionDatabase,
=======
    TBCA,
>>>>>>> Stashed changes
}

fun FoodSourceType.toDomain(): FoodSource.Type =
    when (this) {
        FoodSourceType.User -> FoodSource.Type.User
        FoodSourceType.OpenFoodFacts -> FoodSource.Type.OpenFoodFacts
<<<<<<< Updated upstream
        FoodSourceType.USDA -> FoodSource.Type.USDA
        FoodSourceType.SwissFoodCompositionDatabase -> FoodSource.Type.SwissFoodCompositionDatabase
=======
        FoodSourceType.TBCA -> FoodSource.Type.TBCA
>>>>>>> Stashed changes
    }

fun FoodSource.Type.toEntity(): FoodSourceType =
    when (this) {
        FoodSource.Type.User -> FoodSourceType.User
        FoodSource.Type.OpenFoodFacts -> FoodSourceType.OpenFoodFacts
<<<<<<< Updated upstream
        FoodSource.Type.USDA -> FoodSourceType.USDA
        FoodSource.Type.SwissFoodCompositionDatabase -> FoodSourceType.SwissFoodCompositionDatabase
=======
        FoodSource.Type.TBCA -> FoodSourceType.TBCA
>>>>>>> Stashed changes
    }
