package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.sum

data class DiaryMeal(val meal: Meal, val entries: List<FoodDiaryEntry>) {
    val nutritionFacts: NutritionFacts by lazy { entries.map { it.nutritionFacts }.sum() }
}
