package com.maksimowiczm.foodyou.feature.diary.addfood.measurement.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.addfood.measurement.domain.MeasurableFood
import com.maksimowiczm.foodyou.feature.diary.core.data.food.FoodId
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.Measurement
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal abstract class MeasurementScreenViewModel : ViewModel() {
    abstract val food: StateFlow<MeasurableFood?>
    abstract val selectedMeasurement: StateFlow<Measurement?>
    abstract val eventBus: SharedFlow<MeasurementScreenEvent>
    abstract fun onConfirm(measurement: Measurement)
    abstract fun onDeleteFood(foodId: FoodId)
}
