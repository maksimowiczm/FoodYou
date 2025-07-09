package com.maksimowiczm.foodyou.feature.fooddiary.data

import androidx.room.Embedded
import com.maksimowiczm.foodyou.feature.food.data.Minerals
import com.maksimowiczm.foodyou.feature.food.data.Nutrients
import com.maksimowiczm.foodyou.feature.food.data.Vitamins

data class Food(
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
