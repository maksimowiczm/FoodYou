package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.food.Weighted
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

@Immutable
internal sealed interface FoodModel : Weighted {
    val foodId: FoodId
    val name: String
    val nutritionFacts: NutritionFacts
    val isLiquid: Boolean
    val note: String?
    override val totalWeight: Double?
    override val servingWeight: Double?

    val canUnpack: Boolean
        get() = foodId is FoodId.Recipe
}
