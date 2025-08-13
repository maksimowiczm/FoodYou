package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement

@Immutable
internal sealed interface FoodModel {
    val foodId: FoodId
    val name: String
    val nutritionFacts: NutritionFacts
    val isLiquid: Boolean
    val note: String?

    val canUnpack: Boolean
        get() = foodId is FoodId.Recipe

    fun weight(measurement: Measurement): Double?
}
