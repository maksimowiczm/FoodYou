package com.maksimowiczm.foodyou.feature.addfood.data.model

import com.maksimowiczm.foodyou.feature.product.data.model.Product

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

    /**
     * Replace the quantity suggestion for the given enum.
     *
     * @param weightMeasurement The new weight measurement to replace the quantity suggestion with.
     *
     * @return A new [QuantitySuggestion] with the updated quantity suggestion.
     */
    fun replace(weightMeasurement: WeightMeasurement): QuantitySuggestion {
        val mutable = quantitySuggestions.toMutableMap()

        val enum = weightMeasurement.asEnum()
        mutable[enum] = when (weightMeasurement) {
            is WeightMeasurement.WeightUnit -> weightMeasurement.weight
            is WeightMeasurement.Package -> weightMeasurement.quantity
            is WeightMeasurement.Serving -> weightMeasurement.quantity
        }

        return QuantitySuggestion(product, mutable)
    }
}
