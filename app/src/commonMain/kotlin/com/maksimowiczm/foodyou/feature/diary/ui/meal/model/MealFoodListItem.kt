package com.maksimowiczm.foodyou.feature.diary.ui.meal.model

import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement

data class MealFoodListItem(
    val measurementId: MeasurementId,
    val name: String,
    val brand: String?,
    val calories: Int,
    val proteins: Int,
    val carbohydrates: Int,
    val fats: Int,
    val weight: Float,
    val weightMeasurement: WeightMeasurement
)
