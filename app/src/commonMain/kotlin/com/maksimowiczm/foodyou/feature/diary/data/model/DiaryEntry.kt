package com.maksimowiczm.foodyou.feature.diary.data.model

data class DiaryEntry(
    override val product: Product,
    override val measurement: WeightMeasurement,
    val entryId: Long
) : ProductWithMeasurement
