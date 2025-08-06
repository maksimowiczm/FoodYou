package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared

enum class MeasurementType {
    /** The weight is measured in grams. */
    Gram,

    /** The weight is measured in the packages of the product. */
    Package,

    /** The weight is measured in the servings of the product. */
    Serving,

    /** The weight is measured in milliliters. */
    Milliliter,
}
