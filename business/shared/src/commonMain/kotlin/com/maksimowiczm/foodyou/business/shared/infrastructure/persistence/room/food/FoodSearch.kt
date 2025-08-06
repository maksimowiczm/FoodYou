package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food

import androidx.room.Embedded
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.Minerals
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.Nutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.Vitamins

data class FoodSearch(
    val productId: Long?,
    val recipeId: Long?,
    val headline: String,
    val isLiquid: Boolean,
    @Embedded val nutrients: Nutrients?,
    @Embedded val vitamins: Vitamins?,
    @Embedded val minerals: Minerals?,
    val totalWeight: Float?,
    val servingWeight: Float?,
    val measurementJson: String?,
)
