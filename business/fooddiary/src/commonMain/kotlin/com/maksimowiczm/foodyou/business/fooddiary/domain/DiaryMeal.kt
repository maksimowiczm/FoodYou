package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.entity.Meal
import com.maksimowiczm.foodyou.shared.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.food.sum

data class DiaryMeal(val meal: Meal, val entries: List<DiaryEntry>) {
    val nutritionFacts: NutritionFacts by lazy { entries.map { it.nutritionFacts }.sum() }
}
