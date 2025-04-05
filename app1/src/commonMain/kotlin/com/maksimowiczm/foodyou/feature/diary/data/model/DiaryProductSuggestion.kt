package com.maksimowiczm.foodyou.feature.diary.data.model

data class DiaryProductSuggestion(
    override val product: Product,
    override val measurement: WeightMeasurement
) : ProductWithMeasurement
