package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.MeasurableFood
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal abstract class MeasurementScreenViewModel : ViewModel() {
    abstract val food: StateFlow<MeasurableFood?>
    abstract val selectedMeasurement: StateFlow<Measurement?>
    abstract val eventBus: SharedFlow<MeasurementScreenEvent>
    abstract fun onConfirm(measurement: Measurement)
    abstract fun onDeleteFood(foodId: FoodId)
}
