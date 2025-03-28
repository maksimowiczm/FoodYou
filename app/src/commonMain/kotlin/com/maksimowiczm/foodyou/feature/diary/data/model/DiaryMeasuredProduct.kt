package com.maksimowiczm.foodyou.feature.diary.data.model

data class DiaryMeasuredProduct(
    override val product: Product,
    override val measurement: WeightMeasurement,
    val measurementId: MeasurementId.Product
) : ProductWithMeasurement
