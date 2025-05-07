package com.maksimowiczm.foodyou.core.domain.model

import kotlinx.datetime.LocalDateTime

fun testProductWithMeasurement(
    measurementId: MeasurementId.Product = MeasurementId.Product(1L),
    measurement: Measurement = Measurement.Gram(120f),
    measurementDate: LocalDateTime = LocalDateTime(2025, 5, 7, 12, 0),
    product: Product = testProduct()
) = ProductWithMeasurement(
    measurementId = measurementId,
    measurement = measurement,
    measurementDate = measurementDate,
    product = product
)
