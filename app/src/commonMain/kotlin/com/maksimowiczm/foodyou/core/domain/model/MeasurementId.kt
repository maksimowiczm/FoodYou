package com.maksimowiczm.foodyou.core.domain.model

sealed interface MeasurementId {

    @JvmInline
    value class Product(val id: Long) : MeasurementId

    @JvmInline
    value class Recipe(val id: Long) : MeasurementId
}
