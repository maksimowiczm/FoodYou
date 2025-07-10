package com.maksimowiczm.foodyou.core.util

object NutrientsHelper {
    const val PROTEINS = 4
    const val CARBOHYDRATES = 4
    const val FATS = 9

    fun calculateEnergy(proteins: Float, carbohydrates: Float, fats: Float): Float =
        proteins * PROTEINS + carbohydrates * CARBOHYDRATES + fats * FATS

    fun calculateEnergy(proteins: Double, carbohydrates: Double, fats: Double): Double =
        proteins * PROTEINS + carbohydrates * CARBOHYDRATES + fats * FATS

    fun proteinsPercentage(energy: Int, proteins: Number): Float =
        proteins.toFloat() * PROTEINS / energy

    fun carbohydratesPercentage(energy: Int, carbohydrates: Number): Float =
        carbohydrates.toFloat() * CARBOHYDRATES / energy

    fun fatsPercentage(energy: Int, fats: Number) = fats.toFloat() * FATS / energy

    fun proteinsToEnergy(proteins: Number) = proteins.toFloat() * PROTEINS

    fun carbohydratesToEnergy(carbohydrates: Number) = carbohydrates.toFloat() * CARBOHYDRATES

    fun fatsToEnergy(fats: Number) = fats.toFloat() * FATS

    fun caloriesToKilojoules(calories: Number): Float = calories.toFloat() * 4.184f
}
