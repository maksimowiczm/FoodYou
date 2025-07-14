package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.Food
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
    val weight: Float?
        get() = when (val measurement = measurement) {
            is Measurement.Milliliter -> measurement.value
            is Measurement.Gram -> measurement.value
            is Measurement.Package -> food.totalWeight?.let { it * measurement.quantity }
            is Measurement.Serving -> food.servingWeight?.let { it * measurement.quantity }
        }

    val proteins: Float?
        get() {
            val weight = weight ?: return null
            val proteinValue = food.nutritionFacts.proteins.value ?: return null
            return proteinValue * weight / 100f
        }

    val carbohydrates: Float?
        get() {
            val weight = weight ?: return null
            val carbohydrateValue = food.nutritionFacts.carbohydrates.value ?: return null
            return carbohydrateValue * weight / 100f
        }

    val fats: Float?
        get() {
            val weight = weight ?: return null
            val fatValue = food.nutritionFacts.fats.value ?: return null
            return fatValue * weight / 100f
        }

    val energy: Float?
        get() {
            val weight = weight ?: return null
            val energyValue = food.nutritionFacts.energy.value ?: return null
            return energyValue * weight / 100f
        }
}
