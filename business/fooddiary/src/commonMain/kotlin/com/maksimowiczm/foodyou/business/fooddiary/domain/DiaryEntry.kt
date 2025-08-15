package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class DiaryEntry(
    val id: Long,
    val mealId: Long,
    val date: LocalDate,
    val measurement: Measurement,
    val food: DiaryFood,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    val weight: Double
        get() = food.weight(measurement)

    /**
     * Total nutrition facts for the entry based on the food's nutrition facts and the weight of the
     * measurement.
     */
    val nutritionFacts: NutritionFacts by lazy { weight.div(100).let { food.nutritionFacts * it } }
}
