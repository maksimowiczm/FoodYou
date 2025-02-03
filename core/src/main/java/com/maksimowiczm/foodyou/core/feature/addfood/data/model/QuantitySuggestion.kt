package com.maksimowiczm.foodyou.core.feature.addfood.data.model

import com.maksimowiczm.foodyou.core.feature.product.data.model.Product

data class QuantitySuggestion(
    val product: Product,
    val quantitySuggestions: Map<WeightMeasurementEnum, Float>
) {
    companion object {
        fun defaultSuggestion(): Map<WeightMeasurementEnum, Float> = mapOf(
            WeightMeasurementEnum.WeightUnit to 100f,
            WeightMeasurementEnum.Package to 1f,
            WeightMeasurementEnum.Serving to 1f
        )
    }
}
