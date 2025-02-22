package com.maksimowiczm.foodyou.core.feature.addfood.ui.portion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum

@Composable
fun rememberPortionState(initialSuggestion: QuantitySuggestion? = null): PortionState = remember {
    PortionState(
        initialSuggestion = initialSuggestion
    )
}

@Stable
class PortionState(initialSuggestion: QuantitySuggestion? = null) {
    var suggestion by mutableStateOf(initialSuggestion)

    /**
     * Indicated which weight measurement user has selected.
     */
    var weightMeasurementEnum: WeightMeasurementEnum? by mutableStateOf(null)
}
