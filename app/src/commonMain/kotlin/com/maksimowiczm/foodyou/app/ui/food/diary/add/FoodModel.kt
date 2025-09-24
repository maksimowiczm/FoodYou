package com.maksimowiczm.foodyou.app.ui.food.diary.add

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.food.domain.entity.FoodId

@Immutable
internal sealed interface FoodModel {
    val foodId: FoodId
    val name: String
    val nutritionFacts: NutritionFacts
    val isLiquid: Boolean
    val note: String?
    val totalWeight: Double?
    val servingWeight: Double?

    val canUnpack: Boolean
        get() = foodId is FoodId.Recipe

    fun weight(measurement: Measurement): Double
}
