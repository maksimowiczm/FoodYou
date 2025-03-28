package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.ext.sumOf
import kotlinx.datetime.LocalDate

data class DiaryDay(
    val date: LocalDate,

    /**
     * Map of meals with list of products for each meal.
     */
    val mealProductMap: Map<Meal, List<DiaryMeasuredProduct>>,

    /**
     * Daily goals for the day.
     */
    val dailyGoals: DailyGoals
) {
    /**
     * Total calories for the diary day.
     */
    val totalCalories: Float
        get() = mealProductMap.values.flatten().sumOf { it.calories }

    val totalProteins: Float
        get() = mealProductMap.values.flatten().sumOf { it.proteins }

    val totalCarbohydrates: Float
        get() = mealProductMap.values.flatten().sumOf { it.carbohydrates }

    val totalFats: Float
        get() = mealProductMap.values.flatten().sumOf { it.fats }

    /**
     * List of all meals in the diary day.
     */
    val meals: List<Meal> get() = mealProductMap.keys.toList()
}
