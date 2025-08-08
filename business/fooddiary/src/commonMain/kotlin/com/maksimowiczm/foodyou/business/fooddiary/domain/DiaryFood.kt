package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

sealed interface DiaryFood {
    val name: String

    /** Nutrition facts of the food per 100g or 100ml. */
    val nutritionFacts: NutritionFacts
    val servingWeight: Double?
    val totalWeight: Double?
    val isLiquid: Boolean
}
