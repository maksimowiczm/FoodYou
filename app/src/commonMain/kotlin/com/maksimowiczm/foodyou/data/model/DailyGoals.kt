package com.maksimowiczm.foodyou.data.model

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
    val fats: Float
) {
    /**
     * 100 = 100%
     */
    val proteinsAsPercentage: Float
        get() = proteins * 100

    /**
     * 100 = 100%
     */
    val carbohydratesAsPercentage: Float
        get() = carbohydrates * 100

    /**
     * 100 = 100%
     */
    val fatsAsPercentage: Float
        get() = fats * 100

    /**
     * Proteins goal in grams.
     */
    val proteinsAsGrams: Int
        get() = (calories * proteins / NutrientsHelper.PROTEINS).roundToInt()

    /**
     * Carbohydrates goal in grams.
     */
    val carbohydratesAsGrams: Int
        get() = (calories * carbohydrates / NutrientsHelper.CARBOHYDRATES).roundToInt()

    /**
     * Fats goal in grams.
     */
    val fatsAsGrams: Int
        get() = (calories * fats / NutrientsHelper.FATS).roundToInt()
}

fun defaultGoals() = DailyGoals(
    calories = 2000,
    proteins = .2f,
    carbohydrates = .5f,
    fats = .3f
)
