package com.maksimowiczm.foodyou.core.model

sealed interface MeasurementId {

    @JvmInline
    value class Product(val id: Long) : MeasurementId

    @JvmInline
    value class Recipe(val id: Long) : MeasurementId
}
