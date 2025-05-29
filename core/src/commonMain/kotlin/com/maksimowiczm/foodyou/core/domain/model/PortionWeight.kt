package com.maksimowiczm.foodyou.core.domain.model

/**
 * Represents the measurement unit of a product.
 */
object PortionWeight {

    @JvmInline
    value class Package(val weight: Float)

    @JvmInline
    value class Serving(val weight: Float)
}
