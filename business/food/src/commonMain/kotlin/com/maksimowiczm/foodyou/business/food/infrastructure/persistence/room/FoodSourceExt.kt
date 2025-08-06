package com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room

import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food.FoodSourceType

internal fun FoodSource.Type.toEntity(): FoodSourceType =
    when (this) {
        FoodSource.Type.User -> FoodSourceType.User
        FoodSource.Type.OpenFoodFacts -> FoodSourceType.OpenFoodFacts
        FoodSource.Type.USDA -> FoodSourceType.USDA
        FoodSource.Type.SwissFoodCompositionDatabase -> FoodSourceType.SwissFoodCompositionDatabase
    }
