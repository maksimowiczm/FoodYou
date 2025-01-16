package com.maksimowiczm.foodyou.feature.diary.data.model

import java.time.LocalDate

data class DiaryDay(
    val date: LocalDate,

    /**
     * Map of meals with list of products for each meal.
     */
    val mealProducts: Map<Meal, List<MealProduct>>,

    /**
     * Daily goals for the day.
     */
    val dailyGoals: DailyGoals
) {
    /**
     * Total calories for the meal in the diary day.
     */
    fun totalCalories(meal: Meal) = mealProducts[meal]?.sumOf { it.totalCalories.toInt() } ?: 0

    /**
     * List of all meals in the diary day.
     */
    val meals: List<Meal> get() = Meal.entries
}
