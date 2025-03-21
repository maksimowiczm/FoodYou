package com.maksimowiczm.foodyou.feature.diary.data.model

/**
 * Represents how the weight of the product is measured.
 */
enum class WeightMeasurementEnum {
    /**
     * The weight is measured in the weight unit of the product.
     */
    WeightUnit,

    /**
     * The weight is measured in the packages of the product.
     */
    Package,

    /**
     * The weight is measured in the servings of the product.
     */
    Serving
}
