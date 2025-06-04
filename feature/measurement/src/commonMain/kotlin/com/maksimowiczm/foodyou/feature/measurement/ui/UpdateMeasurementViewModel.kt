package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.FoodRepository
import com.maksimowiczm.foodyou.core.domain.MealRepository
import com.maksimowiczm.foodyou.core.domain.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.core.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.model.Measurement
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
    private val measurementId: Long,
    mealsRepository: MealRepository,
    private val measurementRepository: MeasurementRepository,
    private val foodRepository: FoodRepository
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

    private val eventBus = Channel<MeasurementScreenEvent>()
    val measurementUpdatedEventBus = eventBus.receiveAsFlow()

    fun onUpdateMeasurement(date: LocalDate, mealId: Long, measurement: Measurement) = launch {
        measurementRepository.updateMeasurement(
            measurementId = measurementId,
            date = date,
            mealId = mealId,
            measurement = measurement
        )
        eventBus.send(MeasurementScreenEvent.Done)
    }

    fun onDeleteMeasurement() = launch {
        val measurement = measurement.value ?: return@launch
        val foodId = measurement.food.id

        foodRepository.deleteFood(id = foodId)
        eventBus.send(MeasurementScreenEvent.Deleted)
    }
}
