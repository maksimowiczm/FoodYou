package com.maksimowiczm.foodyou.feature.diary.core.data.food

/**
 * Represents the measurement unit of a product.
 */
object PortionWeight {

    @JvmInline
    value class Package(val weight: Float)

    @JvmInline
    value class Serving(val weight: Float)
}
