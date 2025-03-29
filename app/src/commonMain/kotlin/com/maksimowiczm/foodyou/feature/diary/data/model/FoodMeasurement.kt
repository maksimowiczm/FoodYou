package com.maksimowiczm.foodyou.feature.diary.data.model

data class FoodMeasurement(
    val foodId: FoodId,
    val measurement: WeightMeasurement,
    val measurementId: MeasurementId
)
