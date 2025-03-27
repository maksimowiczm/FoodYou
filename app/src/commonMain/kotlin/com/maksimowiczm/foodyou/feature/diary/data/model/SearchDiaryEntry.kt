package com.maksimowiczm.foodyou.feature.diary.data.model

sealed interface SearchDiaryEntry : ProductWithMeasurement {
    data class Measurement(
        override val product: Product,
        override val measurement: WeightMeasurement,
        val entryId: Long
    ) : SearchDiaryEntry

    data class Suggestion(
        override val product: Product,
        override val measurement: WeightMeasurement
    ) : SearchDiaryEntry
}
