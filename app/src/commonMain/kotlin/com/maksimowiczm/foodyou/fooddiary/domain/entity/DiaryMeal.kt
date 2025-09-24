package com.maksimowiczm.foodyou.fooddiary.domain.entity

import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.sum

data class DiaryMeal(val meal: Meal, val entries: List<DiaryEntry>) {
    val nutritionFacts: NutritionFacts = entries.map { it.nutritionFacts }.sum()
}
