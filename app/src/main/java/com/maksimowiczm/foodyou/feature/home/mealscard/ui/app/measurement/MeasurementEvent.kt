package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.measurement

import com.maksimowiczm.foodyou.data.model.Product
import com.maksimowiczm.foodyou.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.data.model.WeightMeasurementEnum

sealed interface MeasurementEvent {
    data object Empty : MeasurementEvent
    data object Loading : MeasurementEvent
    data object Error : MeasurementEvent

    data class Ready(
        val product: Product,
        val suggestion: QuantitySuggestion,
        val highlight: WeightMeasurementEnum? = null
    ) : MeasurementEvent

    /**
     * User has initiated the measurement processing (e.g. saving the measurement, updating the
     * measurement).
     */
    data object Processing : MeasurementEvent
    data object Success : MeasurementEvent
}
