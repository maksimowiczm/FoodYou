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
    val fats: Float
) {
    /**
     * Proteins goal in grams.
     */
    val proteinsAsGrams: Int
        get() = (calories * proteins / NutrimentsAsGrams.PROTEINS).roundToInt()

    /**
     * Carbohydrates goal in grams.
     */
    val carbohydratesAsGrams: Int
        get() = (calories * carbohydrates / NutrimentsAsGrams.CARBOHYDRATES).roundToInt()

    /**
     * Fats goal in grams.
     */
    val fatsAsGrams: Int
        get() = (calories * fats / NutrimentsAsGrams.FATS).roundToInt()
}

fun defaultGoals() = DailyGoals(
    calories = 2000,
    proteins = .2f,
    carbohydrates = .5f,
    fats = .3f
)
