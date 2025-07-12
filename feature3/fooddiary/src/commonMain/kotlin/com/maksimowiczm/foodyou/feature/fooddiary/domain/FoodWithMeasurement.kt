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
        get() = weight?.let { food.nutritionFacts.proteins.value * it / 100f }

    val carbohydrates: Float?
        get() = weight?.let { food.nutritionFacts.carbohydrates.value * it / 100f }

    val fats: Float?
        get() = weight?.let { food.nutritionFacts.fats.value * it / 100f }

    val energy: Float?
        get() = weight?.let { food.nutritionFacts.energy.value * it / 100f }
}
