package com.maksimowiczm.foodyou.feature.food.data.database.search

import androidx.room.Embedded
import com.maksimowiczm.foodyou.feature.food.data.database.food.Minerals
import com.maksimowiczm.foodyou.feature.food.data.database.food.Nutrients
import com.maksimowiczm.foodyou.feature.food.data.database.food.Vitamins

/**
 * Data class representing a food search result. It can be either a product or a recipe.
 */
data class FoodSearch(
    val productId: Long?,
    val recipeId: Long?,
    val headline: String,
    val isLiquid: Boolean,
    @Embedded
    val nutrients: Nutrients?,
    @Embedded
    val vitamins: Vitamins?,
    @Embedded
    val minerals: Minerals?,
    val totalWeight: Float?,
    val servingWeight: Float?
)
