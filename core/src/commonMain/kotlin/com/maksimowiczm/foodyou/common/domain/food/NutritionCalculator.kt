package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.kilocalories

object NutritionCalculator {
    private const val KCAL_PER_GRAM_PROTEIN = 4.0
    private const val KCAL_PER_GRAM_CARBOHYDRATE = 4.0
    private const val KCAL_PER_GRAM_FAT = 9.0

    /** Calculates the energy content in kilocalories for a given weight of protein. */
    fun calculateProteinCalories(weight: Weight): Energy =
        calculateProteinCalories(weight.grams).kilocalories

    /** Calculates the energy content in kilocalories for a given weight of carbohydrates. */
    fun calculateCarbohydrateCalories(weight: Weight): Energy =
        calculateCarbohydrateCalories(weight.grams).kilocalories

    /** Calculates the energy content in kilocalories for a given weight of fat. */
    fun calculateFatCalories(weight: Weight): Energy =
        calculateFatCalories(weight.grams).kilocalories

    /** Calculates the energy content in kilocalories for a given amount of grams of protein. */
    fun calculateProteinCalories(grams: Double): Double = grams * KCAL_PER_GRAM_PROTEIN

    /**
     * Calculates the energy content in kilocalories for a given amount of grams of carbohydrates.
     */
    fun calculateCarbohydrateCalories(grams: Double): Double = grams * KCAL_PER_GRAM_CARBOHYDRATE

    /** Calculates the energy content in kilocalories for a given amount of grams of fat. */
    fun calculateFatCalories(grams: Double): Double = grams * KCAL_PER_GRAM_FAT

    /**
     * Calculates the total energy content for given weights of protein, carbohydrates, and fats.
     */
    fun calculateTotalCalories(protein: Weight, carbohydrates: Weight, fats: Weight): Energy =
        calculateProteinCalories(protein) +
            calculateCarbohydrateCalories(carbohydrates) +
            calculateFatCalories(fats)

    /**
     * Calculates the total energy content in kilocalories for given grams of protein,
     * carbohydrates, and fats.
     */
    fun calculateTotalCalories(
        proteinGrams: Double,
        carbohydrateGrams: Double,
        fatGrams: Double,
    ): Double =
        calculateProteinCalories(proteinGrams) +
            calculateCarbohydrateCalories(carbohydrateGrams) +
            calculateFatCalories(fatGrams)
}
