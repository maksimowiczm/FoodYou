package com.maksimowiczm.foodyou.feature.addfood.ui.portion

import com.maksimowiczm.foodyou.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.product.data.model.Product

sealed interface PortionUiState {
    data object WaitingForProduct : PortionUiState
    data object ProductNotFound : PortionUiState
    data object LoadingProduct : PortionUiState

    sealed interface StateWithProduct : PortionUiState {
        val product: Product
        val suggestion: QuantitySuggestion
        val highlight: WeightMeasurementEnum?
    }

    data class ProductReady(
        override val product: Product,
        override val suggestion: QuantitySuggestion,
        override val highlight: WeightMeasurementEnum?
    ) : StateWithProduct

    data class CreatingPortion(
        override val product: Product,
        override val suggestion: QuantitySuggestion,
        override val highlight: WeightMeasurementEnum?
    ) : StateWithProduct

    data class Success(
        override val product: Product,
        override val suggestion: QuantitySuggestion,
        override val highlight: WeightMeasurementEnum?
    ) : StateWithProduct
}
