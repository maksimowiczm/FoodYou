package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

internal class UpdateMeasurementViewModel(
    private val measurementId: MeasurementId,
    mealsRepository: MealRepository,
    private val measurementRepository: MeasurementRepository
) : ViewModel() {

    val measurement: StateFlow<FoodWithMeasurement?> = measurementRepository
        .observeMeasurement(measurementId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val meals = mealsRepository.observeMeals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val suggestions = measurement
        .filterNotNull()
        .flatMapLatest { measurementRepository.observeSuggestions(it.food.id) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val eventBus = Channel<Unit>()
    val measurementUpdatedEventBus = eventBus.receiveAsFlow()

    fun onUpdateMeasurement(date: LocalDate, mealId: Long, measurement: Measurement) = launch {
        measurementRepository.updateMeasurement(
            measurementId = measurementId,
            date = date,
            mealId = mealId,
            measurement = measurement
        )
        eventBus.send(Unit)
    }
}
