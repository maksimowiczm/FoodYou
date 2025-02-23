package com.maksimowiczm.foodyou.feature.addfood.ui.portion

import com.maksimowiczm.foodyou.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.feature.product.data.model.Product

sealed interface PortionEvent {
    data object Empty : PortionEvent
    data object Loading : PortionEvent
    data object Error : PortionEvent

    data class Ready(val product: Product, val suggestion: QuantitySuggestion) : PortionEvent

    data object CreatingPortion : PortionEvent
    data object Success : PortionEvent
}
