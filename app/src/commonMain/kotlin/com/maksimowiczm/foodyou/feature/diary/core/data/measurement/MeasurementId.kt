package com.maksimowiczm.foodyou.feature.diary.core.data.measurement

sealed interface MeasurementId {

    @JvmInline
    value class Product(val id: Long) : MeasurementId
}
