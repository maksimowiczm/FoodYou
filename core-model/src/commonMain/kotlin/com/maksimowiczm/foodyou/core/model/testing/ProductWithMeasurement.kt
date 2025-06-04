package com.maksimowiczm.foodyou.core.model.testing

import com.maksimowiczm.foodyou.core.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Product
import kotlinx.datetime.LocalDateTime

fun testProductWithMeasurement(
    measurementId: Long = 1L,
    measurement: Measurement = Measurement.Gram(120f),
    measurementDate: LocalDateTime = LocalDateTime(2025, 5, 7, 12, 0),
    mealId: Long = 0L,
    product: Product = testProduct()
) = FoodWithMeasurement(
    measurementId = measurementId,
    measurement = measurement,
    measurementDate = measurementDate,
    mealId = mealId,
    food = product
)
