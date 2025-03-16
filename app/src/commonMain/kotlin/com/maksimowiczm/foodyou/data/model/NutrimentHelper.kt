package com.maksimowiczm.foodyou.data.model

object NutrimentHelper {
    const val PROTEINS = 4
    const val CARBOHYDRATES = 4
    const val FATS = 9

    fun calculateCalories(proteins: Float, carbohydrates: Float, fats: Float): Float =
        proteins * PROTEINS + carbohydrates * CARBOHYDRATES + fats * FATS

    fun proteinsPercentage(calories: Int, proteins: Number): Float =
        proteins.toFloat() * PROTEINS / calories

    fun carbohydratesPercentage(calories: Int, carbohydrates: Number): Float =
        carbohydrates.toFloat() * CARBOHYDRATES / calories

    fun fatsPercentage(calories: Int, fats: Number) = fats.toFloat() * FATS / calories

    fun proteinsToCalories(proteins: Number) = proteins.toFloat() * PROTEINS

    fun carbohydratesToCalories(carbohydrates: Number) = carbohydrates.toFloat() * CARBOHYDRATES

    fun fatsToCalories(fats: Number) = fats.toFloat() * FATS
}
