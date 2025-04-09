package com.maksimowiczm.foodyou.core.database.entity

/**
 * Represents how the weight of the product is measured.
 */
enum class Measurement {
    /**
     * The weight is measured in grams.
     */
    Gram,

    /**
     * The weight is measured in the packages of the product.
     */
    Package,

    /**
     * The weight is measured in the servings of the product.
     */
    Serving
}
