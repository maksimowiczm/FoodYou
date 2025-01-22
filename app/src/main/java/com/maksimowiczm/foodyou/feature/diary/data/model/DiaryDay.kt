package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import java.time.LocalDate

data class DiaryDay(
    val date: LocalDate,

    /**
     * Map of meals with list of products for each meal.
     */
    val productPotions: Map<Meal, List<Portion>>,

    /**
     * Daily goals for the day.
     */
    val dailyGoals: DailyGoals
) {
    /**
     * Total calories for the meal in the diary day.
     */
    fun totalCalories(meal: Meal) = productPotions[meal]?.sumOf { it.totalCalories.toInt() } ?: 0

    /**
     * List of all meals in the diary day.
     */
    val meals: List<Meal> get() = Meal.entries
}
