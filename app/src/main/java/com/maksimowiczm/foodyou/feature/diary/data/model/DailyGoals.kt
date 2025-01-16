package com.maksimowiczm.foodyou.feature.diary.data.model

import kotlin.math.roundToInt

data class DailyGoals(
    val calories: Int,

    /**
     * Proteins percentage of total calories.
     */
    val proteins: Float,

    /**
     * Carbohydrates percentage of total calories.
     */
    val carbohydrates: Float,

    /**
     * Fats percentage of total calories.
     */
    val fats: Float,

    /**
     * Calorie goal in percentage of total calories for each meal.
     */
    private val mealCalorieGoalMap: Map<Meal, Float>
) {
    /**
     * Calorie goal for a specific meal.
     */
    fun calorieGoal(meal: Meal) = (calories * mealCalorieGoalMap[meal]!!).roundToInt()
}

fun defaultGoals() = DailyGoals(
    calories = 2000,
    proteins = .2f,
    carbohydrates = .5f,
    fats = .3f,
    mealCalorieGoalMap = mapOf(
        Meal.Breakfast to .2f,
        Meal.Lunch to .3f,
        Meal.Dinner to .3f,
        Meal.Snacks to .2f
    )
)
