package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.product.data.model.Product

data class QuantitySuggestion(
    val product: Product,
    val quantitySuggestions: Map<WeightMeasurementEnum, Float>
) {
    companion object {
        val defaultSuggestion: Map<WeightMeasurementEnum, Float> by lazy {
            // Use when to ensure type safety when adding new enum values

            WeightMeasurementEnum.entries.associateWith {
                when (it) {
                    WeightMeasurementEnum.WeightUnit -> 100f
                    WeightMeasurementEnum.Package -> 1f
                    WeightMeasurementEnum.Serving -> 1f
                }
            }
        }
    }
}
