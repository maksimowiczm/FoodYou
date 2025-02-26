package com.maksimowiczm.foodyou.feature.legacy.addfood.ui.portion

import com.maksimowiczm.foodyou.data.model.Product
import com.maksimowiczm.foodyou.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.data.model.WeightMeasurementEnum

sealed interface PortionEvent {
    data object Empty : PortionEvent
    data object Loading : PortionEvent
    data object Error : PortionEvent

    data class Ready(
        val product: Product,
        val suggestion: QuantitySuggestion,
        val highlight: WeightMeasurementEnum? = null
    ) : PortionEvent

    /**
     * User has initiated the portion processing (e.g. saving the portion, updating the portion).
     */
    data object Processing : PortionEvent
    data object Success : PortionEvent
}
