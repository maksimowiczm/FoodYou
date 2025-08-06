package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts

data class DiaryFoodProduct(
    override val name: String,
    override val nutritionFacts: NutritionFacts,
    override val servingWeight: Double?,
    override val totalWeight: Double?,
) : DiaryFood
