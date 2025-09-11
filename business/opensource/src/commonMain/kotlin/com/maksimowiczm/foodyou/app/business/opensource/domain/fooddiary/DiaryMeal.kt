package com.maksimowiczm.foodyou.app.business.opensource.domain.fooddiary

import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.entity.Meal
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.shared.domain.food.sum

data class DiaryMeal(val meal: Meal, val entries: List<DiaryEntry>) {
    val nutritionFacts: NutritionFacts by lazy { entries.map { it.nutritionFacts }.sum() }
}
