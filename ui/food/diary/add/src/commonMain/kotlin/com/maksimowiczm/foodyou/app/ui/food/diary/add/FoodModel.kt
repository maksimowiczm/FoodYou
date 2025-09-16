package com.maksimowiczm.foodyou.app.ui.food.diary.add

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.food.Weighted

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
