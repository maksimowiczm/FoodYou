package com.maksimowiczm.foodyou.feature.diary.data.model

interface MeasurementSuggestion {
    val packageSuggestion: WeightMeasurement.Package?
    val servingSuggestion: WeightMeasurement.Serving?
    val weightSuggestion: WeightMeasurement.WeightUnit
}
