package com.maksimowiczm.foodyou.feature.diary.data.model

sealed interface MeasurementId {
    data class Product(val measurementId: Long) : MeasurementId
    data class Recipe(val measurementId: Long) : MeasurementId
}
