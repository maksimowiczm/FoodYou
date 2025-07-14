package com.maksimowiczm.foodyou.feature.food.data

import androidx.room.Embedded

/**
 * Data class representing a food search result. It can be either a product or a recipe.
 */
data class FoodSearch(
    val productId: Long?,
    val recipeId: Long?,
    val headline: String,
    @Embedded
    val nutrients: Nutrients,
    @Embedded
    val vitamins: Vitamins,
    @Embedded
    val minerals: Minerals,
    val totalWeight: Float?,
    val servingWeight: Float?
)
