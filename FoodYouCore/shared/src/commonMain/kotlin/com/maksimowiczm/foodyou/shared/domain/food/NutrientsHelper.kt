package com.maksimowiczm.foodyou.shared.domain.food

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

    fun proteinsPercentageToGrams(energy: Int, proteinsPercentage: Double): Double =
        (proteinsPercentage * energy) / PROTEINS

    fun carbohydratesPercentageToGrams(energy: Int, carbohydratesPercentage: Double): Double =
        (carbohydratesPercentage * energy) / CARBOHYDRATES

    fun fatsPercentageToGrams(energy: Int, fatsPercentage: Double): Double =
        (fatsPercentage * energy) / FATS

    fun caloriesToKilojoules(calories: Number): Double = calories.toDouble() * 4.184
}
