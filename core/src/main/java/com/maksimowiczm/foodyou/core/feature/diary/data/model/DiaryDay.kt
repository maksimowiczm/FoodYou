package com.maksimowiczm.foodyou.core.feature.diary.data.model

import com.maksimowiczm.foodyou.core.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import java.time.LocalDate

data class DiaryDay(
    val date: LocalDate,

    /**
     * Map of meals with list of products for each meal.
     */
    val mealProductMap: Map<Meal, List<ProductWithWeightMeasurement>>,

    /**
     * Daily goals for the day.
     */
    val dailyGoals: DailyGoals
) {
    /**
     * Total calories for the meal in the diary day.
     */
    fun totalCalories(meal: Meal) = mealProductMap[meal]?.sumOf { it.calories } ?: 0

    val totalCalories: Int
        get() = mealProductMap.values.flatten().sumOf { it.calories }

    val totalProteins: Int
        get() = mealProductMap.values.flatten().sumOf { it.proteins }

    val totalCarbohydrates: Int
        get() = mealProductMap.values.flatten().sumOf { it.carbohydrates }

    val totalFats: Int
        get() = mealProductMap.values.flatten().sumOf { it.fats }

    /**
     * List of all meals in the diary day.
     */
    val meals: List<Meal> get() = Meal.entries
}
