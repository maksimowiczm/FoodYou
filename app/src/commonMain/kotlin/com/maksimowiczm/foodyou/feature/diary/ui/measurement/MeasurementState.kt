package com.maksimowiczm.foodyou.feature.diary.ui.measurement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.maksimowiczm.foodyou.feature.diary.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum

@Composable
fun rememberMeasurementState(initialSuggestion: QuantitySuggestion? = null) = remember {
    MeasurementState(
        initialSuggestion = initialSuggestion
    )
}

@Stable
class MeasurementState(initialSuggestion: QuantitySuggestion? = null) {
    var suggestion by mutableStateOf(initialSuggestion)

    /**
     * Indicated which weight measurement user has selected.
     */
    var weightMeasurementEnum: WeightMeasurementEnum? by mutableStateOf(null)
}
