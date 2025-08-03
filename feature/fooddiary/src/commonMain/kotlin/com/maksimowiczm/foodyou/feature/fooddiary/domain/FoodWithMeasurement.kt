package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.Food
import com.maksimowiczm.foodyou.feature.food.domain.NutritionFacts
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.datetime.LocalDate

data class FoodWithMeasurement(
    val measurementId: Long,
    val measurement: Measurement,
    val measurementDate: LocalDate,
    val mealId: Long,
    val food: Food
) {
    /**
     * Weight in grams. If weight is null then measurement is invalid. (e.g. 1 x package while
     * product has no package weight)
     */
    val weight: Float? by lazy {
        when (val measurement = measurement) {
            is Measurement.Milliliter -> measurement.value
            is Measurement.Gram -> measurement.value
            is Measurement.Package -> food.totalWeight?.let { it * measurement.quantity }
            is Measurement.Serving -> food.servingWeight?.let { it * measurement.quantity }
        }
    }

    val nutritionFacts: NutritionFacts? by lazy {
        val weight = weight ?: return@lazy null
        food.nutritionFacts * weight / 100f
    }

    val proteins: Float? by lazy {
        val weight = weight ?: return@lazy null
        val proteinValue = food.nutritionFacts.proteins.value ?: return@lazy null
        proteinValue * weight / 100f
    }

    val carbohydrates: Float? by lazy {
        val weight = weight ?: return@lazy null
        val carbohydrateValue = food.nutritionFacts.carbohydrates.value ?: return@lazy null
        carbohydrateValue * weight / 100f
    }

    val fats: Float? by lazy {
        val weight = weight ?: return@lazy null
        val fatValue = food.nutritionFacts.fats.value ?: return@lazy null
        fatValue * weight / 100f
    }

    val energy: Float? by lazy {
        val weight = weight ?: return@lazy null
        val energyValue = food.nutritionFacts.energy.value ?: return@lazy null
        energyValue * weight / 100f
    }
}
