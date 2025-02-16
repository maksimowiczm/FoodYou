package com.maksimowiczm.foodyou.core.feature.addfood.ui.portion

import com.maksimowiczm.foodyou.core.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product

sealed interface PortionUiState {
    data object Empty : PortionUiState
    data object Loading : PortionUiState
    data object Error : PortionUiState

    sealed interface WithProduct : PortionUiState {
        val product: Product
        val suggestion: QuantitySuggestion
    }

    data class Ready(
        override val product: Product,
        override val suggestion: QuantitySuggestion
    ) : WithProduct

    sealed interface WithMeasurement : WithProduct {
        val measurement: WeightMeasurementEnum
    }

    data class CreatingPortion(
        override val product: Product,
        override val suggestion: QuantitySuggestion,
        override val measurement: WeightMeasurementEnum
    ) : WithMeasurement

    data class Success(
        override val product: Product,
        override val suggestion: QuantitySuggestion,
        override val measurement: WeightMeasurementEnum
    ) : WithMeasurement
}
