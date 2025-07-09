package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement

data class FoodMeasurement(
    val foodId: FoodId,
    val measurementId: Long,
    val measurement: Measurement
)
