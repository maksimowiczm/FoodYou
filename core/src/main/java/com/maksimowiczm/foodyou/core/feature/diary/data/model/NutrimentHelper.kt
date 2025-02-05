package com.maksimowiczm.foodyou.core.feature.diary.data.model

object NutrimentHelper {
    const val PROTEINS = 4
    const val CARBOHYDRATES = 4
    const val FATS = 9

    fun calculateCalories(proteins: Float, carbohydrates: Float, fats: Float): Float {
        return proteins * PROTEINS + carbohydrates * CARBOHYDRATES + fats * FATS
    }

    fun proteinsPercentage(calories: Int, proteins: Number): Float {
        return proteins.toFloat() * PROTEINS / calories
    }

    fun carbohydratesPercentage(calories: Int, carbohydrates: Number): Float {
        return carbohydrates.toFloat() * CARBOHYDRATES / calories
    }

    fun fatsPercentage(calories: Int, fats: Number) = fats.toFloat() * FATS / calories

    fun proteinsToCalories(proteins: Number) = proteins.toFloat() * PROTEINS

    fun carbohydratesToCalories(carbohydrates: Number) = carbohydrates.toFloat() * CARBOHYDRATES

    fun fatsToCalories(fats: Number) = fats.toFloat() * FATS
}
